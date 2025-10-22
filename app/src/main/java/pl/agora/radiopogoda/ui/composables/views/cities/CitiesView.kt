package pl.agora.radiopogoda.ui.composables.views.cities

import androidx.compose.foundation.background
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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Job
import pl.agora.radiopogoda.data.api.ApiResult
import pl.agora.radiopogoda.data.model.channel.Channel
import pl.agora.radiopogoda.ui.theme.secondary
import pl.agora.radiopogoda.ui.theme.white
import pl.agora.radiopogoda.ui.theme.yellow
import pl.agora.radiopogoda.ui.uiData.MainUiState
import pl.agora.radiopogoda.utils.Consts.MAIN_CHANNEL_ID

@Composable
fun CitiesView(
    mainUiState: State<MainUiState>,
    onChannelCard: (Channel) -> Unit,
    saveCity: (String) -> Job
) = Box(Modifier.fillMaxWidth().background(white)) {

    val channelsState = mainUiState.value.channels

    when(channelsState) {
        is ApiResult.Success -> {
            CitiesGrid(
                channelsState.value.filterNot { it.node_id == MAIN_CHANNEL_ID }.reversed(),
                onChannelCard,
                mainUiState,
                saveCity
            )
        }
        else -> {
            CircularProgressIndicator(Modifier.align(Alignment.Center), color = white)
        }
    }
}

@Composable
fun CitiesGrid(
    channels: List<Channel>,
    onChannelCard: (Channel) -> Unit,
    mainUiState: State<MainUiState>,
    saveCity: (String) -> Job
) {

    val pickedChannelState = mainUiState.value.cities.collectAsStateWithLifecycle(null)

    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(channels.size) { index ->
            CityButton(
                channels[index].title.split(" ").let {
                        words -> if (words.size > 3) words.drop(3).joinToString(" ") else channels[index].title
                },
                pickedChannelState.value == channels[index].title
            ) {
                saveCity(channels[index].title)
                onChannelCard(channels[index])
            }
        }
    }
}

@Composable
fun CityButton(name: String, city: Boolean, onClick: () -> Unit) = Box(Modifier.fillMaxWidth()) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(4.dp).fillMaxSize(),
        colors = ButtonDefaults.buttonColors(backgroundColor = if (!city) secondary else yellow)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = name.uppercase(),
                color = white,
                maxLines = 2,
                minLines = 2,
                fontSize = 16.sp,
                modifier = Modifier.fillMaxSize(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    }
}