package pl.agora.radiopogoda.ui.composables.views.player

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.data.api.ApiResult
import pl.agora.radiopogoda.data.model.channel.Channel
import pl.agora.radiopogoda.ui.composables.views.channel.CityButton
import pl.agora.radiopogoda.ui.uiData.MainUiState
import pl.agora.radiopogoda.utils.Consts

@Composable
fun PlayerChannelsBottomSheet(
    mainUiState: State<MainUiState>,
    onChannelCard: (Channel) -> Unit,
) = Column(
    modifier = Modifier.fillMaxSize(),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Text(
        text = stringResource(R.string.music_channels).uppercase(),
        fontWeight = FontWeight.Medium,
        fontSize = 15.5.sp,
        modifier = Modifier.padding(top = 20.dp, bottom = 12.dp),
        color = MaterialTheme.colorScheme.onPrimary
    )
    ChannelsGrid(mainUiState, onChannelCard)
}

@Composable
private fun ChannelsGrid(mainUiState: State<MainUiState>, onChannelCard: (Channel) -> Unit) {
    val channelsState = mainUiState.value.channels

    if (channelsState is ApiResult.Success<List<Channel>>) {
        val list = remember {
            channelsState.value.filterNot { it.node_id == Consts.MAIN_CHANNEL_ID }.reversed()
        }

        Column(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            list.forEach { channel ->
                CityButton(
                    name = channel.title,
                    onClick = { onChannelCard(channel) },
                )
            }
        }
    }
}


