package pl.agora.radiopogoda.ui.composables.views.onRadio

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.agora.radiopogoda.data.api.ApiResult
import pl.agora.radiopogoda.ui.composables.customViews.ErrorView
import pl.agora.radiopogoda.ui.uiData.MainUiState
import pl.agora.radiopogoda.utils.getActualInfoFromList
import pl.agora.radiopogoda.R

private val MORNING_ON_RADIO_THRESHOLD = (21600..46799).toList()
private val DAY_ON_RADIO_THRESHOLD = (46800..64799).toList()

@Composable
fun OnRadioView(mainUiState: State<MainUiState>) = Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center,
) {
    when (val radioPrograms = mainUiState.value.radioPrograms) {
        is ApiResult.Loading ->
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primaryContainer
            )

        is ApiResult.Failure -> ErrorView(
            text = stringResource(R.string.retrofit_error),
            modifier = Modifier.width(250.dp),
        )

        is ApiResult.Success -> {
            val list = radioPrograms.value
            val activeProgram = list.getActualInfoFromList()

            val morning = remember {
                list.filter { it.start in MORNING_ON_RADIO_THRESHOLD }
            }

            val day = remember {
                list.filter { it.start in DAY_ON_RADIO_THRESHOLD }
            }

            val night = remember {
                list.filter { it !in morning && it !in day }
            }

            Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                OnRadioSection(stringResource(R.string.morning_category), morning, activeProgram)
                OnRadioSection(stringResource(R.string.day_category), day, activeProgram)
                OnRadioSection(stringResource(R.string.night_category), night, activeProgram)
                Spacer(Modifier.height(64.dp))
            }
        }
    }
}