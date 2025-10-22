package pl.agora.radiopogoda.ui.composables.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import pl.agora.radiopogoda.ui.composables.views.player.SongsColumn
import pl.agora.radiopogoda.ui.composables.views.player.TimeRow
import pl.agora.radiopogoda.ui.viewModels.HistoryViewModel

@Composable
fun MusicListView() = Column(Modifier.fillMaxWidth()) {
    val viewModel = hiltViewModel<HistoryViewModel>()

    val historyState = viewModel.historyChannel.collectAsStateWithLifecycle()
    val timeState = viewModel.timeSliderState.collectAsStateWithLifecycle()

    val getActualHour = remember { { viewModel.getActualHour() } }
    val decreaseSliderTime = remember { { viewModel.decreaseSliderTime() } }
    val increaseSliderTime = remember { { viewModel.increaseSliderTime() } }

    TimeRow(timeState, getActualHour, decreaseSliderTime, increaseSliderTime)
    HorizontalDivider(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 12.dp, start = 16.dp, end = 16.dp),
        color = MaterialTheme.colorScheme.secondary
    )
    SongsColumn(historyState, true)
}