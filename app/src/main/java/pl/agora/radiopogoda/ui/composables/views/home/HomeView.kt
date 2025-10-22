package pl.agora.radiopogoda.ui.composables.views.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.StateFlow
import pl.agora.radiopogoda.data.api.ApiResult
import pl.agora.radiopogoda.data.model.news.NewsModel
import pl.agora.radiopogoda.data.wrappers.PlayerMediaItemModel
import pl.agora.radiopogoda.infrastructure.services.music.MusicServiceState
import pl.agora.radiopogoda.ui.advert.AdvertViewBig
import pl.agora.radiopogoda.ui.composables.customViews.player.PlayCardSection
import pl.agora.radiopogoda.ui.uiData.MainUiState

@Composable
fun HomeView(
    navController: NavHostController,
    mainUiState: State<MainUiState>,
    onPlayButton: (PlayerMediaItemModel) -> Unit,
    getNewsState: () -> StateFlow<ApiResult<List<NewsModel>>>,
    playerDataState: State<PlayerMediaItemModel?>,
    serviceState: State<MusicServiceState>,
) {
    val playerPadding = if (serviceState.value != MusicServiceState.NOT_INIT) 64.dp  else 3.dp

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PlayCardSection(mainUiState, playerDataState, serviceState, onPlayButton)
        NewsSection(navController, getNewsState)
        Spacer(Modifier.padding(3.dp))
        AdvertViewBig()
        Spacer(Modifier.padding(3.dp))
        PodcastSection(mainUiState, navController, onPlayButton)
        Spacer(Modifier.padding(3.dp))
        AdvertViewBig()
        Spacer(Modifier.padding(3.dp))
        AudioBookSection(mainUiState, navController, onPlayButton)
        Spacer(Modifier.height(playerPadding))

    }
}