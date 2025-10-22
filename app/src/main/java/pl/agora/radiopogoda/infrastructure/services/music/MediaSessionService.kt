package pl.agora.radiopogoda.infrastructure.services.music

import pl.agora.radiopogoda.data.wrappers.PlayerMediaItemModel
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.KeyEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import pl.agora.radiopogoda.R
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.reflect.KFunction1

@Singleton
class MediaSessionService @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private var mediaSession: MediaSessionCompat? = null

    fun setupMediaSession(
        playPause: () -> Unit,
        play: () -> Unit,
        pause: () -> Unit,
        getServiceState: () -> MusicServiceState,
        playFromMediaId: KFunction1<String, Unit>
    ) {
        mediaSession?.release()

        mediaSession = MediaSessionCompat(context, context.getString(R.string.app_name)).apply {
            setCallback(object : MediaSessionCompat.Callback() {

                override fun onMediaButtonEvent(mediaButtonEvent: Intent): Boolean {
                    val event = mediaButtonEvent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
                    event?.takeIf { it.action == KeyEvent.ACTION_DOWN }?.let { keyEvent ->
                        when (keyEvent.keyCode) {
                            KeyEvent.KEYCODE_HEADSETHOOK, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> playPause()
                            KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                                if (getServiceState() in listOf(
                                        MusicServiceState.PLAY,
                                        MusicServiceState.PREPARE
                                    )) {
                                    pause()
                                }
                            }
                            KeyEvent.KEYCODE_MEDIA_PLAY -> {
                                if (getServiceState() in listOf(
                                        MusicServiceState.PAUSE,
                                        MusicServiceState.PAUSED_FROM_RECEIVERS
                                    )) {
                                    play()
                                }
                            }
                        }
                    }
                    return super.onMediaButtonEvent(mediaButtonEvent)
                }

                override fun onPause() {
                    super.onPause()
                    if (MusicService.state.value != MusicServiceState.PLAY) play() else pause()
                }

                override fun onPlay() {
                    super.onPlay()
                    if (MusicService.state.value != MusicServiceState.PLAY) play() else pause()
                }


                override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
                    super.onPlayFromMediaId(mediaId, extras)
                    mediaId?.let { id -> playFromMediaId(id) }
                }

            })
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
            isActive = true
        }
    }

    fun setMetadataToSession(data: PlayerMediaItemModel) {
        mediaSession?.setMetadata(
            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, data.imageUri)
                .putString(MediaMetadataCompat.METADATA_KEY_ART_URI, data.imageUri)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, data.imageUri)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, data.title)
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, data.title)
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, data.author)
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, data.author)
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, data.uri)
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, Long.MAX_VALUE)
                .build()
        )
    }

     fun updatePlaybackState(isPlaying: Boolean) {
        val playbackState = if (isPlaying) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
        mediaSession?.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE)
                .setState(
                    playbackState,
                    PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                    1f
                )
                .setBufferedPosition(PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN)
                .setExtras(Bundle().apply {
                    putLong(MediaMetadataCompat.METADATA_KEY_DURATION, Long.MAX_VALUE)
                })
                .build()
        )
    }

    fun getSessionToken() = mediaSession?.sessionToken

    fun releaseSession() {
        mediaSession?.release()
        mediaSession = null
    }
}