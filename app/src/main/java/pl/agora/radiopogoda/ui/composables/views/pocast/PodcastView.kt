package pl.agora.radiopogoda.ui.composables.views.pocast

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.agora.radiopogoda.ui.composables.customViews.ErrorView
import pl.agora.radiopogoda.ui.advert.AdvertViewBig
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.data.api.ApiResult
import pl.agora.radiopogoda.data.wrappers.PlayerMediaItemModel
import pl.agora.radiopogoda.infrastructure.services.music.MusicServiceState
import pl.agora.radiopogoda.ui.theme.white
import pl.agora.radiopogoda.ui.uiData.MainUiState

@Composable
fun PodcastView(
    mainUiState: State<MainUiState>,
    onPodcastCard: (PlayerMediaItemModel) -> Unit,
    onProgramCard: (String) -> Unit,
    serviceState: State<MusicServiceState>,
    playerDataState: State<PlayerMediaItemModel?>,
) {
    val configuration = LocalConfiguration.current
    val width = remember { configuration.screenWidthDp / 2.5 }

    val pickedPodcast = mainUiState.value.pickedProgramPodcasts

    LazyColumn(Modifier.fillMaxSize()) {
        when (val programs = mainUiState.value.programs) {
            is ApiResult.Loading ->
                item {
                    Box(Modifier.fillMaxWidth().height(200.dp)) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.primaryContainer
                        )
                    }
                }

            is ApiResult.Failure ->
                item {
                    Box(Modifier.fillMaxWidth()) {
                        ErrorView(
                            text = stringResource(R.string.retrofit_error),
                            modifier = Modifier.width(250.dp).align(Alignment.Center),
                        )
                    }
                }

            is ApiResult.Success ->
                item {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(12.dp)
                    ) {
                        items(
                            count = programs.value.size,
                            key = { index -> programs.value[index].node_id }
                        ) { index ->
                            Box(Modifier.width(width.dp)) {
                                ProgramCard(
                                    onClick = { onProgramCard(programs.value[index].node_id) },
                                    imageUrl = programs.value[index].image.link,
                                    title = programs.value[index].title,
                                    programId = programs.value[index].node_id
                                )
                                if (pickedPodcast == programs.value[index].node_id) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(Alignment.Center),
                                        color = white
                                    )
                                }
                            }
                        }
                    }
                }
        }

        when (val podcasts = mainUiState.value.podcasts) {
            is ApiResult.Loading ->
                item {
                    Box(Modifier.fillMaxWidth().height(200.dp)) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.primaryContainer
                        )
                    }
                }

            is ApiResult.Failure ->
                item {
                    Box(Modifier.fillMaxWidth()) {
                        ErrorView(
                            text = stringResource(R.string.retrofit_error),
                            modifier = Modifier.width(250.dp).align(Alignment.Center),
                        )
                    }
                }

            is ApiResult.Success ->
                items(
                    count = podcasts.value.data.size,
                    key = { podcasts.value.data[it].node_id }
                ) {
                    Column {
                        if (it == 3) { AdvertViewBig(Modifier.fillMaxWidth().padding(12.dp)) }
                        PodcastCard(
                            podcast = podcasts.value.data[it],
                            onPodcast = onPodcastCard,
                            serviceState = serviceState,
                            playerMediaDataState = playerDataState,
                            showProgramName = true
                        )
                    }
                }
        }
        item { Spacer(Modifier.height(64.dp)) }
    }
}