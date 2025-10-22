package pl.agora.radiopogoda.ui.composables.views.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.data.model.history.History
import pl.agora.radiopogoda.ui.theme.whiteTransparent
import pl.agora.radiopogoda.ui.viewModels.HistoryViewModel

@Composable
fun PlayerLiveBottomSheet() {
    val viewModel = hiltViewModel<HistoryViewModel>()

    val historyState = viewModel.historyChannel.collectAsStateWithLifecycle()
    val timeState = viewModel.timeSliderState.collectAsStateWithLifecycle()

    val getActualHour = remember { { viewModel.getActualHour() } }
    val decreaseSliderTime = remember { { viewModel.decreaseSliderTime() } }
    val increaseSliderTime = remember { { viewModel.increaseSliderTime() } }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.check_playlist).uppercase(),
            fontWeight = FontWeight.Medium,
            fontSize = 15.5.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(top = 20.dp, bottom = 12.dp)
        )
        TimeRow(timeState, getActualHour, decreaseSliderTime, increaseSliderTime)
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
            color = MaterialTheme.colorScheme.secondary
        )
        SongsColumn(historyState)
    }
}

@Composable
fun TimeRow(
    timeState: State<Pair<Int, Int>>,
    getActualHour: () -> Int,
    decreaseSliderTime: () -> Unit,
    increaseSliderTime: () -> Unit
) {
    val previousState = timeState.value.first > 0
    val secondState = timeState.value.second < getActualHour().inc() && timeState.value.first < 23

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { decreaseSliderTime() },
            modifier = Modifier.padding(6.dp),
            enabled = previousState
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = null,
                tint = if (previousState) MaterialTheme.colorScheme.onPrimary else whiteTransparent
            )
        }
        Text(
            text = "${timeState.value.first}:00 - ${timeState.value.second}:00",
            fontWeight = FontWeight.Light,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
        IconButton(
            onClick = { increaseSliderTime() },
            modifier = Modifier.padding(6.dp),
            enabled = secondState
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_forward),
                contentDescription = null,
                tint = if (secondState) MaterialTheme.colorScheme.onPrimary else whiteTransparent
            )
        }
    }
}

@Composable
fun SongsColumn(historyState: State<List<History>?>, scrollable: Boolean = false) {
    val value = historyState.value
    Box(modifier = Modifier.fillMaxSize()) {
        when {
            value == null -> {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.TopCenter).padding(20.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
            value.isEmpty() -> {
                Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
                    Text(
                        text = stringResource(R.string.history_empty),
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center).padding(30.dp)
                    )
                }
            }
            else -> {
                val modifier = if (scrollable)
                    Modifier.fillMaxSize().defaultMinSize(minHeight = 250.dp).padding(8.dp)
                    .verticalScroll(rememberScrollState())
                else
                    Modifier.fillMaxSize().defaultMinSize(minHeight = 250.dp).padding(8.dp)

                Column(
                    modifier = modifier,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    value.filter { it.rds_artist.isNotEmpty() }.forEach { SongRow(history = it) }
                    Spacer(Modifier.height(64.dp))
                }
            }
        }
    }
}

@Composable
fun SongRow(history: History) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = history.getTime(),
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(12.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Image(
            modifier = Modifier.size(64.dp),
            painter = rememberAsyncImagePainter(
                model = history.img,
                error = painterResource(R.drawable.logo)
            ),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.padding(6.dp)) {
            Text(
                text = history.rds_title,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = history.rds_artist,
                color = MaterialTheme.colorScheme.tertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}