package pl.agora.radiopogoda.infrastructure.player

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.util.Log
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.gemius.sdk.stream.EventProgramData
import com.gemius.sdk.stream.Player.EventType
import com.gemius.sdk.stream.PlayerData
import com.gemius.sdk.stream.ProgramData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.data.wrappers.PlayerMediaItemModel
import pl.agora.radiopogoda.infrastructure.ads.IABTCFProvider
import pl.agora.radiopogoda.infrastructure.analytics.IAnalyticsPlayerManager
import pl.agora.radiopogoda.utils.Consts
import pl.agora.radiopogoda.infrastructure.services.music.LockService
import pl.agora.radiopogoda.infrastructure.services.music.MediaType
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RadioPlayer @Inject constructor(
    private val lockService: LockService,
    private val audioManager: AudioManager,
    @ApplicationContext context: Context,
): IPlayer, IAnalyticsPlayerManager<EventType> {

    private var exoPlayer: ExoPlayer? = null
    private var progressJob: Job? = null
    private val lock = Any()

    private val podcastDuration = MutableStateFlow(0f)
    private val actualPlayerPosition = MutableStateFlow(0)

    private val player: com.gemius.sdk.stream.Player by lazy {
        com.gemius.sdk.stream.Player(
            context.getString(R.string.gemius_app_name),
            Consts.GEMIUS_URL,
            context.getString(R.string.gemius_stream_id),
            PlayerData()
        ).apply { setContext(context) }
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun initPlayer(context: Context, errorListener: Player.Listener) {
        val mediaUri = playerMediaData.value?.uri
        requireNotNull(mediaUri) { "Invalid media uri argument" }

        val consent = IABTCFProvider.provideIABTCF(context)
        val uri = mediaUri.formatUriStringToConsent(consent)

        exoPlayer = ExoPlayer.Builder(context)
            .setAudioAttributes(AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).build(), false)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(context).setLiveTargetOffsetMs(Consts.LIVE_TARGET_OFFSET_MS)
            ).build().apply {
                val mediaItem: MediaItem = MediaItem.Builder()
                    .setUri(uri)
                    .setLiveConfiguration(
                        MediaItem.LiveConfiguration.Builder().setMaxPlaybackSpeed(1.0f).build()
                    )
                    .build()
                setMediaItem(mediaItem)
                addListener(errorListener)
            }
        lockService.lockCPU()
    }

    override fun destroyPlayer() {
        progressJob?.cancel()
        exoPlayer?.release()
        exoPlayer = null
        lockService.unlockCPU()
    }

    override fun play() {
        exoPlayer?.play()
    }

    override fun play(context: Context, errorListener: Player.Listener) {
        synchronized (lock) {
            try {
                if (exoPlayer == null) { initPlayer(context, errorListener) }
                exoPlayer?.prepare()
            } catch (e: Exception) {
                destroyPlayer()
                e.printStackTrace()
            }
        }
    }

    override fun rewindForward() = performRewind { current, rewindValue -> current + rewindValue }

    override fun rewindBack() = performRewind { current, rewindValue -> current - rewindValue }

    override fun rewindSeek(rewindValue: Float) = performRewind { _, _ ->
        (rewindValue * podcastDuration.value).toInt()
    }

    private fun performRewind(calcPosition: (current: Int, rewindValue: Int) -> Int) {
        progressJob?.cancel()
        val currentPos = actualPlayerPosition.value
        val newPos = calcPosition(currentPos, Consts.REWIND_VALUE)
        actualPlayerPosition.value = newPos.coerceAtLeast(0)
        podcastDuration.value.takeIf { it > 0 }?.let { duration ->
            playerPosition.value = actualPlayerPosition.value / duration
        }
        exoPlayer?.let {
            it.seekTo(actualPlayerPosition.value.toLong())
            startPodcastProgressUpdater()
        }
    }

    private fun startPodcastProgressUpdater() {
        progressJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive && exoPlayer != null) {
                exoPlayer?.let { player ->
                    actualPlayerPosition.value = player.currentPosition.toInt()
                    if (player.duration > 0) {
                        playerPosition.value = player.currentPosition.toFloat() / player.duration.toFloat()
                    }
                }
                delay(1000)
            }
        }
    }

    override fun seekPodcastToActualPosition() {
        exoPlayer?.let { player ->
            player.seekTo(actualPlayerPosition.value.toLong())
            player.play()
            podcastDuration.value = player.duration.toFloat()
            startPodcastProgressUpdater()
        }
    }

    override fun setMediaData(data: PlayerMediaItemModel?) {
        val currentData = playerMediaData.value
        if (data != null && data.uri != currentData?.uri) { onNewProgram(data) }
        playerMediaData.value = data
    }

    override fun resetPlayer() {
        playerPosition.value = 0f
        actualPlayerPosition.value = 0
    }

    override fun onNewProgram(mediaItem: PlayerMediaItemModel) {
        val mediaType = mediaItem.mediaType

        val programName = if (mediaType != MediaType.PODCAST) mediaItem.subtitle
        else mediaItem.title
        Log.d("ZXC", programName)

        val typeTransmission = if (mediaType == MediaType.PODCAST) 1 else 2

        val data = ProgramData().apply {
            name = programName
            programType = ProgramData.ProgramType.AUDIO
            duration = mediaItem.duration
            volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
            transmissionType = typeTransmission
            transmissionStartTime = (Instant.now().epochSecond).toString()

            if (mediaType != MediaType.PODCAST) {
                transmissionChannel = mediaItem.subtitle
            } else {
                mediaItem.nodeId?.let { series = it }
            }
        }
        player.newProgram(mediaItem.songId, data)
    }

    override fun onNewPlayerEvent(eventType: EventType) {
        playerMediaData.value?.let { mediaItem ->
            val offset = if (mediaItem.mediaType == MediaType.PODCAST)
                exoPlayer?.currentPosition?.div(1000)?.toInt() ?: 0
            else
                (System.currentTimeMillis() / 1000).toInt()

            Log.d("ZXC", eventType.name + " $offset")

            when (eventType) {
                EventType.NEXT -> {
                    player.programEvent(
                        mediaItem.songId, offset, eventType, EventProgramData().apply {
                            listID = mediaItem.nodeId
                            programDuration = mediaItem.duration
                            autoPlay = true
                            volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                        }
                    )
                }
                else -> {
                    player.programEvent(mediaItem.songId, offset, eventType, EventProgramData().apply {
                        autoPlay = false
                        programDuration = mediaItem.duration
                        volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                    })
                }
            }
        }
    }

    private fun String.formatUriStringToConsent(consent: Pair<String?, Int>): String =
        if (this.contains("dist=zet")) {
            this.replace("dist=zet", "dist=zet_app")
                .plus("&gdpr_consent=${consent.first}")
                .plus("&gdpr=${consent.second}")
        } else this

    companion object {
        private val playerPosition = MutableStateFlow(0f)
        private val playerMediaData = MutableStateFlow<PlayerMediaItemModel?>(null)

        val mediaData: StateFlow<PlayerMediaItemModel?> = playerMediaData
        val position: StateFlow<Float> = playerPosition
    }
}