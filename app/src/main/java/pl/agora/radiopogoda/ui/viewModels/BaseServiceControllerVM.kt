package pl.agora.radiopogoda.ui.viewModels

import android.app.PendingIntent.CanceledException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pl.agora.radiopogoda.data.wrappers.PlayerMediaItemModel
import pl.agora.radiopogoda.data.wrappers.PlayerMediaItemModel.Companion.MEDIA_KEY
import pl.agora.radiopogoda.infrastructure.player.RadioPlayer
import pl.agora.radiopogoda.infrastructure.services.music.MusicService
import pl.agora.radiopogoda.infrastructure.services.music.MusicServiceActions
import pl.agora.radiopogoda.ui.uiData.MainUiState
import pl.agora.radiopogoda.utils.getPendingIntent
import pl.agora.radiopogoda.utils.isAppInForeground

abstract class BaseServiceControllerVM(@ApplicationContext private val context: Context): ViewModel() {

    protected val _mainUiState = MutableStateFlow(MainUiState())
    val mainUiState = _mainUiState.asStateFlow()

    val serviceState = MusicService.state
    val playerPositionState: StateFlow<Float> = RadioPlayer.position
    val playerMediaData: StateFlow<PlayerMediaItemModel?> = RadioPlayer.mediaData

    private fun sendServiceIntent(
        action: MusicServiceActions,
        configureExtras: (Bundle.() -> Unit)? = null
    ) {
        val intent = Intent(context, MusicService::class.java).apply {
            this.action = action.name
            if (configureExtras != null) {
                putExtras(Bundle().apply(configureExtras))
            }
        }
        try {
            intent.getPendingIntent(context).send()
        } catch (e: CanceledException) {
            e.printStackTrace()
        }
    }

    protected fun onServiceNotInit(playerMediaItemModel: PlayerMediaItemModel) {
        val startIntent = Intent(context, MusicService::class.java).apply {
            action = MusicServiceActions.START_ACTION.name
            putExtras(Bundle().apply { putParcelable(MEDIA_KEY, playerMediaItemModel) })
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !context.isAppInForeground()) {
            return
        }
        context.startForegroundService(startIntent)
    }

    protected fun onServicePlayOrPrepare(playerMediaItemModel: PlayerMediaItemModel) {
        sendServiceIntent(MusicServiceActions.PAUSE_ACTION) {
            putParcelable(MEDIA_KEY, playerMediaItemModel)
        }
    }

    protected fun onServicePause(playerMediaItemModel: PlayerMediaItemModel) {
        sendServiceIntent(MusicServiceActions.PLAY_ACTION) {
            putParcelable(MEDIA_KEY, playerMediaItemModel)
        }
    }

    protected fun onTrackChanged(playerMediaItemModel: PlayerMediaItemModel?) {
        sendServiceIntent(MusicServiceActions.ON_TRACK_CHANGED) {
            putParcelable(MEDIA_KEY, playerMediaItemModel)
        }
    }

    fun onDestroyService() {
        sendServiceIntent(MusicServiceActions.STOP_ACTION)
    }

    fun onSeek(action: MusicServiceActions, value: Float? = null) {
        sendServiceIntent(action) {
            value?.let { putFloat("rewind", it) }
        }
        if (action == MusicServiceActions.REWIND_SEEK) {
            _mainUiState.update { it.copy(isSeekCompleted = true) }
        }
    }

    fun onSeekEnded() { _mainUiState.update { it.copy(isSeekCompleted = false) } }
}