package pl.agora.radiopogoda.ui.composables.views.channel

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.data.api.ApiResult
import pl.agora.radiopogoda.data.model.channel.Channel
import pl.agora.radiopogoda.ui.composables.customViews.ErrorView
import pl.agora.radiopogoda.ui.uiData.MainUiState

@Composable
fun MusicChannelsView(
    mainUiState: State<MainUiState>,
    onChannelCard: (Channel) -> Unit
) = Box(Modifier.fillMaxSize()) {
    when (mainUiState.value.channels) {
        is ApiResult.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

        is ApiResult.Failure ->
            ErrorView(
                text = stringResource(R.string.retrofit_error),
                modifier = Modifier.width(250.dp).align(Alignment.Center),
            )

        is ApiResult.Success -> {
            ChannelsGrid(mainUiState, onChannelCard)
        }
    }
}