package pl.agora.radiopogoda.infrastructure.snackbar


interface ISnackManager {
    fun addMessage(message: SnackbarMessage)
    fun removeMessage()
}