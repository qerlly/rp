package pl.agora.radiopogoda.ui.composables.views.channel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.agora.radiopogoda.data.api.ApiResult
import pl.agora.radiopogoda.data.model.channel.Channel
import pl.agora.radiopogoda.ui.theme.black
import pl.agora.radiopogoda.ui.theme.indicatorTransparent
import pl.agora.radiopogoda.ui.theme.lightGray
import pl.agora.radiopogoda.ui.theme.secondary
import pl.agora.radiopogoda.ui.theme.white
import pl.agora.radiopogoda.ui.theme.whiteCTransparent
import pl.agora.radiopogoda.ui.theme.whiteTransparent
import pl.agora.radiopogoda.ui.uiData.MainUiState
import pl.agora.radiopogoda.utils.Consts.MAIN_CHANNEL_ID

@Composable
fun ChannelsGrid(mainUiState: State<MainUiState>, onChannelCard: (Channel) -> Unit) {

    val channels = mainUiState.value.channels
    if (channels is ApiResult.Success<List<Channel>>) {
        val filteredList = channels.value.filterNot { it.node_id == MAIN_CHANNEL_ID }.reversed()
        CitiesGrid(filteredList, onChannelCard)
    }
}

@Composable
fun CitiesGrid(channels: List<Channel>, onChannelCard: (Channel) -> Unit, ) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(1),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(channels.size) { index ->
            CityButton(
                channels[index].title.split(" ").let {
                        words -> if (words.size > 3) words.drop(3).joinToString(" ") else channels[index].title
                }
            ) {
              onChannelCard(channels[index])
            }
        }
    }
}

@Composable
fun CityButton(name: String, onClick: () -> Unit) = Box(Modifier.fillMaxWidth()) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxSize(),
        colors = ButtonDefaults.buttonColors(whiteCTransparent)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = name.uppercase(),
                color = black,
                fontSize = 16.sp,
                modifier = Modifier.fillMaxSize().padding(12.dp),
                textAlign = TextAlign.Left,
                fontWeight = FontWeight.Medium
            )
        }
    }
}