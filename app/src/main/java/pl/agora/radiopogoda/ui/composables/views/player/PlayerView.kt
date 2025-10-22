package pl.agora.radiopogoda.ui.composables.views.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.Job
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.data.api.ApiResult
import pl.agora.radiopogoda.data.model.channel.Channel
import pl.agora.radiopogoda.data.model.podcast.Podcasts
import pl.agora.radiopogoda.data.wrappers.PlayerMediaItemModel
import pl.agora.radiopogoda.infrastructure.services.music.MediaType
import pl.agora.radiopogoda.infrastructure.services.music.MusicServiceActions
import pl.agora.radiopogoda.infrastructure.services.music.MusicServiceState
import pl.agora.radiopogoda.ui.composables.customViews.ErrorView
import pl.agora.radiopogoda.ui.composables.views.pocast.PodcastCard
import pl.agora.radiopogoda.ui.theme.white
import pl.agora.radiopogoda.ui.uiData.MainUiState

@Composable
fun PlayerView(
    closeDrawer: () -> Job,
    playerDataState: State<PlayerMediaItemModel?>,
    serviceState: State<MusicServiceState>,
    mainUiState: State<MainUiState>,
    playerPositionState: State<Float>,
    onPlayButton: (PlayerMediaItemModel) -> Unit,
    onChannelCard: (Channel) -> Unit,
    onSeekEnded: () -> Unit,
    onSeek: (MusicServiceActions, Float?) -> Unit,
) {
    val podcastsState = mainUiState.value.currentProgramPodcasts

    LazyColumn(Modifier.fillMaxSize().background(white)) {

        if (serviceState.value == MusicServiceState.NOT_INIT) closeDrawer()

        item {
            PlayerCard(
                drawerAction = closeDrawer,
                playerDataState = playerDataState,
                mainUiState = mainUiState,
                serviceState = serviceState,
                playerPositionState = playerPositionState,
                onSeekEnded = onSeekEnded,
                onSeek = onSeek,
                onPlayButton = onPlayButton,
            )
        }

        when (playerDataState.value?.mediaType) {
            null -> closeDrawer()
            MediaType.PODCAST ->
                playerPodcastsBottomSheet(
                    podcastsState = podcastsState,
                    playerData = playerDataState,
                    serviceState = serviceState,
                    onPlayButton = onPlayButton,
                )

            MediaType.MAIN_CHANNEL -> item {
                PlayerLiveBottomSheet()
            }

            MediaType.CHANNELS -> item {
                PlayerChannelsBottomSheet(
                    mainUiState = mainUiState,
                    onChannelCard = onChannelCard
                )
            }
        }
    }
}

fun LazyListScope.playerPodcastsBottomSheet(
    podcastsState: ApiResult<Podcasts>,
    playerData: State<PlayerMediaItemModel?>,
    serviceState: State<MusicServiceState>,
    onPlayButton: (PlayerMediaItemModel) -> Unit,
) {

    when (podcastsState) {
        ApiResult.Loading -> {
            item {
                Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.TopCenter).padding(20.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                }
            }
        }

        ApiResult.Failure -> {
            item {
                Box(modifier = Modifier.fillMaxSize()) {
                    ErrorView(
                        text = stringResource(R.string.retrofit_error),
                        modifier = Modifier.width(250.dp).align(Alignment.Center)
                    )
                }
            }
        }

        is ApiResult.Success<Podcasts> -> {
            val podcasts = podcastsState.value.data

            items(count = podcasts.size, key = { podcasts[it].node_id }) {
                PodcastCard(
                    podcast = podcasts[it],
                    playerMediaDataState = playerData,
                    serviceState = serviceState,
                    onPodcast = onPlayButton,
                )
            }
        }
    }
}


@Composable
private fun PlayerCard(
    drawerAction: () -> Job,
    playerDataState: State<PlayerMediaItemModel?>,
    mainUiState: State<MainUiState>,
    serviceState: State<MusicServiceState>,
    playerPositionState: State<Float>,
    onSeekEnded: () -> Unit,
    onSeek: (MusicServiceActions, Float?) -> Unit,
    onPlayButton: (PlayerMediaItemModel) -> Unit,
) {
    when (val playerData = playerDataState.value) {
        null -> drawerAction()
        else -> Card(
            shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
            colors = CardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
            elevation = CardDefaults.cardElevation(8.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(
                        onClick = { drawerAction() },
                        modifier = Modifier.padding(end = 6.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_colapse),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Card(
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    modifier = Modifier.size(140.dp),
                ) {
                    CoilImage(
                        imageModel = {
                            mainUiState.value.radioProgram?.program?.image?.link
                                ?: playerDataState.value?.imageUri
                        },
                        failure = {
                            Image(
                                modifier = Modifier.matchParentSize(),
                                painter = painterResource(R.drawable.logo),
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.fillMaxSize(),
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        ),
                    )
                }
                PlayerTextTitles(mainUiState, playerData)
                PlayerTopCardSection(
                    playerData = playerData,
                    serviceState = serviceState,
                    playerPositionState = playerPositionState,
                    onSeekEnded = onSeekEnded,
                    mainUiState = mainUiState,
                    onSeek = onSeek,
                    onPlayButton = onPlayButton,
                )
            }
        }
    }
}

