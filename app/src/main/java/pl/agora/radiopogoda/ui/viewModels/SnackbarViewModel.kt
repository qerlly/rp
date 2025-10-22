package pl.agora.radiopogoda.ui.viewModels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import pl.agora.radiopogoda.infrastructure.snackbar.SnackbarManager
import javax.inject.Inject

@HiltViewModel
class SnackbarViewModel @Inject constructor(
    private val snackbarManager: SnackbarManager
): ViewModel() {

    val snackbarMessages = snackbarManager.snackbarMessages

    fun removeMessage() = snackbarManager.removeMessage()
}