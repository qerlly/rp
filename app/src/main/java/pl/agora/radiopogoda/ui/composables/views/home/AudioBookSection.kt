package pl.agora.radiopogoda.ui.composables.views.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.data.api.ApiResult
import pl.agora.radiopogoda.data.model.podcast.Podcast.Companion.toMediaData
import pl.agora.radiopogoda.data.wrappers.PlayerMediaItemModel
import pl.agora.radiopogoda.ui.composables.customViews.ErrorView
import pl.agora.radiopogoda.ui.composables.navigation.Destinations
import pl.agora.radiopogoda.ui.uiData.MainUiState
import pl.agora.radiopogoda.utils.openDestination

@Composable
fun AudioBookSection(
    mainUiState: State<MainUiState>,
    navController: NavHostController,
    onPlayButton: (PlayerMediaItemModel) -> Unit
) {
    HomeSection(
        text = stringResource(R.string.audiobooks),
        onClick = { navController.openDestination(Destinations.audobooks) }
    ) {
        val podcastsState = mainUiState.value.audioPodcasts

        val configuration = LocalConfiguration.current
        val loadingModifier = remember {
            Modifier.fillMaxHeight().width(configuration.screenWidthDp.dp)
                .defaultMinSize(minHeight = (configuration.screenHeightDp / 3.4).dp)
        }

        when (podcastsState) {
            is ApiResult.Loading ->
                Box(loadingModifier) { CircularProgressIndicator(Modifier.align(Alignment.Center)) }

            is ApiResult.Failure ->
                Box(Modifier.fillMaxHeight().width(configuration.screenWidthDp.dp)) {
                    ErrorView(
                        text = stringResource(R.string.retrofit_error),
                        modifier = Modifier.width(250.dp).align(Alignment.Center),
                    )
                }

            is ApiResult.Success -> {
                val list = remember { podcastsState.value.data.take(20) }
                val cardWidth = configuration.screenWidthDp / 2.8
                val cardHeight = cardWidth + 65.0

                LazyRow(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { Spacer(Modifier.padding(2.dp)) }
                    items(
                        count = list.size,
                        key = { list[it].node_id },
                    ) {
                        SliderCard(
                            modifier = Modifier.width(cardWidth.dp).height(cardHeight.dp)
                                .clickable { onPlayButton(list[it].toMediaData()) },
                            imageUrl = list[it].program?.image?.link ?: "",
                            upperTitle = list[it].program?.title ?: "",
                            title = list[it].title
                        )
                    }
                    item { Spacer(Modifier.padding(2.dp)) }
                }
            }
        }
    }
}