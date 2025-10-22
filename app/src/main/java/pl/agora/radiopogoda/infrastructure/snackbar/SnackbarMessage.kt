package pl.agora.radiopogoda.infrastructure.snackbar

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarDuration

sealed class SnackbarMessage(
    @StringRes open val titleId: Int,
    @StringRes open val descriptionId: Int,
    @DrawableRes open val drawableId: Int,
    open val duration: SnackbarDuration = SnackbarDuration.Short,
)
