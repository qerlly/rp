package pl.agora.radiopogoda.infrastructure.services.music

import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v4.media.MediaBrowserCompat
import androidx.media.MediaBrowserServiceCompat
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import com.gemius.sdk.stream.Player.EventType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import pl.agora.radiopogoda.data.wrappers.PlayerMediaItemModel
import pl.agora.radiopogoda.infrastructure.player.RadioPlayer
import pl.agora.radiopogoda.infrastructure.receivers.ReceiversManager
import pl.agora.radiopogoda.infrastructure.services.notification.RadioNotificationService
import pl.agora.radiopogoda.utils.auto.AndroidAutoDataProvider
import pl.agora.radiopogoda.utils.isAppInForeground
import androidx.media.utils.MediaConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat(), AudioManager.OnAudioFocusChangeListener {

    @Inject lateinit var notificationService: RadioNotificationService
    @Inject lateinit var mediaSessionService: MediaSessionService
    @Inject lateinit var player: RadioPlayer
    @Inject lateinit var receiversManager: ReceiversManager
    @Inject lateinit var audioManager: AudioManager
    @Inject lateinit var androidAutoDataProvider: AndroidAutoDataProvider

    private val serviceJob = SupervisorJob()
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private var rdsJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? {
        intent?.let {
            if (SERVICE_INTERFACE == it.action) {
                return super.onBind(intent)
            }
        }
        return null
    }

    private fun updateRdsForAuto() {
        if (rdsJob?.isActive == true) return

        rdsJob = scope.launch {
            while (isActive) {
                androidAutoDataProvider.pickedChannel.value?.let { channel ->
                    notifyChildrenChanged(channel)
                    val info = withContext(Dispatchers.IO) {
                        androidAutoDataProvider.getPlayerTitleInfoModel(channel)
                    }
                    if (info != null) {
                        mediaSessionService.setMetadataToSession(info)
                    }
                }

                notifyChildrenChanged(autoRoot)
                delay(30_000L)
            }
        }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        if (clientPackageName.contains("android", ignoreCase = true) ||
            clientPackageName.contains("car", ignoreCase = true)) {
            scope.launch { androidAutoDataProvider.getChannelsList { notifyChildrenChanged(autoRoot) } }
            updateRdsForAuto()
        }

        val extras = Bundle().apply {
            putInt(
                MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_BROWSABLE,
                MediaConstants.DESCRIPTION_EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM
            )
            putInt(
                MediaConstants.DESCRIPTION_EXTRAS_KEY_CONTENT_STYLE_PLAYABLE,
                MediaConstants.DESCRIPTION_EXTRAS_VALUE_CONTENT_STYLE_GRID_ITEM
            )
        }

        return BrowserRoot(autoRoot, extras)
    }


    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        result.detach()
        scope.launch {
            val mediaItems = when (parentId) {
                autoRoot -> androidAutoDataProvider.provideChannels()
                else -> androidAutoDataProvider.provideChannelRds(parentId)
            }
            if (isActive) result.sendResult(mediaItems)
        }
    }

    override fun onCreate() {
        super.onCreate()
        mediaSessionService.setupMediaSession(
            playPause = ::togglePlayPause,
            play = ::onPlay,
            pause = ::onPause,
            getServiceState = { state.value },
            playFromMediaId = ::getAutoMetadataForMediaSession
        )
        sessionToken = mediaSessionService.getSessionToken()

        receiversManager.observeState(
            play = ::onPlay,
            pause = ::onPause,
            getServiceState = { state.value }
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return stopAndDestroy()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !isAppInForeground() && state.value == MusicServiceState.NOT_INIT) {
            return stopAndDestroy()
        }

        startForegroundService()

        val action: MusicServiceActions? = if (setParams(intent))
            MusicServiceActions.START_ACTION
        else
            intent.action?.runCatching { MusicServiceActions.valueOf(this) }?.getOrNull()

        when (action) {
            MusicServiceActions.START_ACTION,
            MusicServiceActions.PLAY_ACTION,
            MusicServiceActions.PLAY_NOTIFICATION -> onPlay()
            MusicServiceActions.PAUSE_ACTION,
            MusicServiceActions.PAUSE_NOTIFICATION -> onPause()
            MusicServiceActions.REWIND_BACK -> player.rewindBack().also {
                player.onNewPlayerEvent(EventType.SEEK)
            }
            MusicServiceActions.REWIND_FORWARD -> player.rewindForward().also {
                player.onNewPlayerEvent(EventType.SEEK)
            }
            MusicServiceActions.REWIND_SEEK -> {
                player.onNewPlayerEvent(EventType.SEEK)
                val rewindFactor = intent.extras?.getFloat("rewind") ?: 0.0f
                player.rewindSeek(rewindFactor)
            }
            MusicServiceActions.STOP_ACTION -> return stopAndDestroy()
            MusicServiceActions.ON_TRACK_CHANGED -> onTrackChanged()
            else -> return stopAndDestroy()
        }
        return START_NOT_STICKY
    }

    override fun onAudioFocusChange(focusChange: Int) = handleAudioFocusChange(focusChange)

    override fun onDestroy() {
        stopAndDestroy()
        mediaSessionService.releaseSession()
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopAndDestroy()
        super.onTaskRemoved(rootIntent)
    }

    private fun startForegroundService() {
        val notification = notificationService.prepareNotification(state.value, RadioPlayer.mediaData.value)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                FOREGROUND_SERVICE_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            startForeground(FOREGROUND_SERVICE_ID, notification)
        }
    }

    private fun onPlay() = try {
        if (requestAudioFocus()) {
            mutableState.value = MusicServiceState.PREPARE
            startForegroundService()
            player.destroyPlayer()
            player.initPlayer(this, playerListener)
            player.play(this, playerListener)
        } else {
            stopAndDestroy()
        }
    } catch (e: Exception) { stopAndDestroy() }

    private fun onPause(serviceState: MusicServiceState = MusicServiceState.PAUSE) {
        val event = if (RadioPlayer.mediaData.value?.mediaType == MediaType.PODCAST)
            EventType.PAUSE
        else
            EventType.STOP
        player.onNewPlayerEvent(event)
        mutableState.value = serviceState
        startForegroundService()
        player.destroyPlayer()
        notificationService.postNotification(serviceState, RadioPlayer.mediaData.value)
    }

    private fun stopAndDestroy(): Int {
        scope.cancel()
        rdsJob?.cancel()
        player.onNewPlayerEvent(EventType.CLOSE)
        mediaSessionService.releaseSession()
        receiversManager.unregisterReceivers()
        audioManager.abandonAudioFocus(this)
        mutableState.value = MusicServiceState.NOT_INIT
        player.resetPlayer()
        player.destroyPlayer()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
        return START_NOT_STICKY
    }

    private fun setParams(intent: Intent): Boolean {
        val data: PlayerMediaItemModel? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(PlayerMediaItemModel.MEDIA_KEY, PlayerMediaItemModel::class.java)
        } else {
            intent.getParcelableExtra(PlayerMediaItemModel.MEDIA_KEY)
        }
        if (intent.action in listOf(
                MusicServiceActions.REWIND_BACK.name,
                MusicServiceActions.REWIND_FORWARD.name,
                MusicServiceActions.REWIND_SEEK.name,
                MusicServiceActions.PAUSE_NOTIFICATION.name,
                MusicServiceActions.PLAY_NOTIFICATION.name
            )
        ) return false

        return if (data?.uri != RadioPlayer.mediaData.value?.uri) {
            player.resetPlayer()
            player.setMediaData(data)
            true
        } else {
            player.setMediaData(data)
            false
        }
    }

    private fun onTrackChanged() {
        notificationService.postNotification(state.value, RadioPlayer.mediaData.value)
    }

    private fun togglePlayPause() {
        if (state.value == MusicServiceState.PAUSE || state.value == MusicServiceState.PAUSED_FROM_RECEIVERS)
            onPlay()
        else
            onPause()
    }

    private fun requestAudioFocus(): Boolean {
        return audioManager.requestAudioFocus(
            this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN
        ) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    private fun handleAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT, AudioManager.AUDIOFOCUS_LOSS -> {
                if (state.value in listOf(MusicServiceState.PLAY, MusicServiceState.PREPARE))
                    onPause(MusicServiceState.PAUSED_FROM_RECEIVERS)
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (state.value == MusicServiceState.PAUSED_FROM_RECEIVERS) onPlay()
            }
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            handlePlaybackError(error)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_ENDED -> {
                    player.onNewPlayerEvent(EventType.COMPLETE)
                    handlePlaybackCompletion()
                }
                Player.STATE_READY ->  {
                    handlePlaybackReady()
                }
                else ->  {}
            }
        }
    }

    private fun handlePlaybackError(error: PlaybackException) {
        error.printStackTrace()
        stopAndDestroy()
    }

    private fun handlePlaybackCompletion() {
        if (RadioPlayer.mediaData.value?.mediaType == MediaType.PODCAST) {
            mutableState.value = MusicServiceState.WAIT_FOR_NEXT_PODCAST
            player.onNewPlayerEvent(EventType.NEXT)
        } else {
            stopAndDestroy()
        }
    }

    private fun handlePlaybackReady() {
        if (state.value != MusicServiceState.PLAY) {
            mutableState.value = MusicServiceState.PLAY
            notificationService.postNotification(state.value, RadioPlayer.mediaData.value)
            if (RadioPlayer.mediaData.value?.mediaType == MediaType.PODCAST) {
                player.seekPodcastToActualPosition()
            } else {
                player.play()
            }
            player.onNewPlayerEvent(EventType.PLAY)
        }
    }

    private fun getAutoMetadataForMediaSession(id: String) {
        scope.launch {
            onPause()
            val data = withContext(Dispatchers.IO) {
                androidAutoDataProvider.getPlayerTitleInfoModel(id)
            }
            if (data != null && data.uri != RadioPlayer.mediaData.value?.uri) {
                player.resetPlayer()
                player.setMediaData(data)
                mediaSessionService.setMetadataToSession(data)
            }
            mediaSessionService.updatePlaybackState(true)
            onPlay()
        }
    }

    companion object {
        const val FOREGROUND_SERVICE_ID = 5488231

        const val autoRoot = "ZPRoot"

        private val mutableState = MutableStateFlow(MusicServiceState.NOT_INIT)
        val state: StateFlow<MusicServiceState> = mutableState
    }
}

enum class MusicServiceState { NOT_INIT, PAUSE, PLAY, PREPARE, WAIT_FOR_NEXT_PODCAST, PAUSED_FROM_RECEIVERS }

enum class MusicServiceActions {
    START_ACTION, PAUSE_ACTION, PLAY_ACTION,
    STOP_ACTION, REWIND_BACK, REWIND_FORWARD, REWIND_SEEK,
    PAUSE_NOTIFICATION, PLAY_NOTIFICATION, ON_TRACK_CHANGED
}

enum class MediaType { CHANNELS, PODCAST, MAIN_CHANNEL }