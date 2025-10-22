package pl.agora.radiopogoda.ui.composables.views.contest

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.agora.radiopogoda.ui.advert.AdvertViewBig
import pl.agora.radiopogoda.ui.composables.customViews.ErrorView
import pl.agora.radiopogoda.data.api.ApiResult
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.ui.uiData.MainUiState

@ExperimentalMaterialApi
@Composable
fun ContestView(mainUiState: State<MainUiState>) {
    Box(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

        val context = LocalContext.current

        when (val contests = mainUiState.value.contests) {
            is ApiResult.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

            is ApiResult.Failure ->
                ErrorView(
                    text = stringResource(R.string.retrofit_error),
                    modifier = Modifier
                        .width(250.dp)
                        .align(Alignment.Center),
                )

            is ApiResult.Success -> {
                val list = contests.value.contests
                if (list.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        list.forEachIndexed { index, active ->
                            if (index == 1)
                                AdvertViewBig(Modifier.fillMaxWidth().padding(12.dp))
                            Box(Modifier.aspectRatio(16f / 9f).fillMaxWidth()) {
                                ContestCard(
                                    active.url,
                                    active.link,
                                    context,
                                )
                            }
                        }
                        Spacer(Modifier.height(64.dp))
                    }
                } else {
                    Text(
                        text = stringResource(R.string.empty_contest),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}
