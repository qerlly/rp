package pl.agora.radiopogoda.ui.composables.views.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.navigation.NavHostController
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.data.api.ApiResult
import pl.agora.radiopogoda.data.model.channel.Channel
import pl.agora.radiopogoda.ui.composables.customViews.ErrorView
import pl.agora.radiopogoda.ui.composables.navigation.Destinations
import pl.agora.radiopogoda.ui.uiData.MainUiState
import pl.agora.radiopogoda.utils.Consts
import pl.agora.radiopogoda.utils.openDestination

@Composable
fun ChannelsSection(
    navController: NavHostController,
    mainUiState: State<MainUiState>,
    onChannelCard: (Channel) -> Unit
) {
    HomeSection(
        text = stringResource(R.string.music_channels),
        onClick = { navController.openDestination(Destinations.channels) }
    ) {
        val channelsState = mainUiState.value.channels

        val configuration = LocalConfiguration.current
        val loadingModifier = remember {
            Modifier.fillMaxHeight().width(configuration.screenWidthDp.dp)
                .defaultMinSize(minHeight = (configuration.screenHeightDp / 3.4).dp)
        }

        when (channelsState) {
            is ApiResult.Loading ->
                Box(loadingModifier) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                }

            is ApiResult.Failure ->
                Box(Modifier.fillMaxHeight().width(configuration.screenWidthDp.dp)) {
                    ErrorView(
                        text = stringResource(R.string.retrofit_error),
                        modifier = Modifier.width(250.dp).align(Alignment.Center),
                    )
                }

            is ApiResult.Success -> {
                val list = remember {
                    channelsState.value.filterNot { it.node_id == Consts.MAIN_CHANNEL_ID }.reversed()
                }

                LazyRow(modifier = Modifier.fillMaxHeight(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    item { Spacer(Modifier.padding(2.dp)) }
                    items(
                        count = list.size,
                        key = { list[it].node_id },
                    ) {
                        Box(Modifier.width(170.dp).aspectRatio(3f / 4f)) {
                            SliderCard(
                                modifier = Modifier.fillMaxSize().clickable {
                                    onChannelCard(list[it])
                                },
                                imageUrl = list[it].image,
                                title = list[it].title
                            )
                        }
                    }
                    item { Spacer(Modifier.padding(2.dp)) }
                }
            }
        }
    }
}