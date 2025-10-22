package pl.agora.radiopogoda.infrastructure.snackbar

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SnackbarManager @Inject constructor(): ISnackManager {

    private val _snackbarMessages = MutableStateFlow<SnackbarMessage?>(null)
    val snackbarMessages: StateFlow<SnackbarMessage?> = _snackbarMessages.asStateFlow()

    override fun addMessage(message: SnackbarMessage) {
        _snackbarMessages.value = message
    }

    override fun removeMessage() {
        _snackbarMessages.value = null
    }
}