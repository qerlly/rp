package pl.agora.radiopogoda.ui.composables.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.Job
import pl.agora.radiopogoda.data.model.channel.Channel
import pl.agora.radiopogoda.data.wrappers.PlayerMediaItemModel
import pl.agora.radiopogoda.infrastructure.services.music.MusicServiceState
import pl.agora.radiopogoda.ui.composables.views.MusicListView
import pl.agora.radiopogoda.ui.composables.views.audioBook.AudioBookView
import pl.agora.radiopogoda.ui.composables.views.channel.MusicChannelsView
import pl.agora.radiopogoda.ui.composables.views.cities.CitiesView
import pl.agora.radiopogoda.ui.composables.views.contact.ContactView
import pl.agora.radiopogoda.ui.composables.views.contest.ContestView
import pl.agora.radiopogoda.ui.composables.views.home.HomeView
import pl.agora.radiopogoda.ui.composables.views.news.NewsView
import pl.agora.radiopogoda.ui.composables.views.onRadio.OnRadioView
import pl.agora.radiopogoda.ui.composables.views.pocast.PodcastView
import pl.agora.radiopogoda.ui.uiData.MainUiState
import pl.agora.radiopogoda.ui.viewModels.NewsViewModel

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterialApi::class)
@Composable
fun Navigation(
    navController: NavHostController,
    resetConsent: () -> Unit,
    mainUiState: State<MainUiState>,
    serviceState: State<MusicServiceState>,
    onChannelCard: (Channel) -> Unit,
    onPlayButton: (PlayerMediaItemModel) -> Unit,
    playerDataState: State<PlayerMediaItemModel?>,
    onProgramCard: (String) -> Unit,
    saveCity: (String) -> Job,
    onAudioProgramCard: (String) -> Unit,
) {

    val newsViewModel = hiltViewModel<NewsViewModel>()
    val getAllState = remember { { newsViewModel.getAllState() } }
    val getNewsState = remember { { url: String -> newsViewModel.getState(url) } }
    val onNewsTabChanged = remember {
        { page: Int ->
            newsViewModel.onTabChanged(TabItem.getNewsTabs()[page].tabName)
        }
    }

    NavHost(
        navController = navController,
        startDestination = Destinations.home,
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary),
    ) {
        composable(
            route = Destinations.home,
        ) {
            HomeView(
                navController = navController,
                mainUiState = mainUiState,
                onPlayButton = onPlayButton,
                getNewsState = getAllState,
                playerDataState = playerDataState,
                serviceState = serviceState,
            )
        }

        composable(
            route = Destinations.news,
        ) { NewsView(onNewsTabChanged, getNewsState) }

        composable(
            route = Destinations.channels,
        ) { MusicChannelsView(mainUiState, onChannelCard) }

        composable(
            route = Destinations.contact,
        ) { ContactView(resetConsent, navController) }

        composable(
            route = Destinations.musicList,
        ) { MusicListView() }

        composable(
            route = Destinations.onRadio,
        ) { OnRadioView(mainUiState) }

        composable(
            route = Destinations.contest,
        ) { ContestView(mainUiState) }

        composable(
            route = Destinations.cities,
        ) { CitiesView(mainUiState, onChannelCard, saveCity)  }

        composable(
            route = Destinations.podcast,
        ) { PodcastView(mainUiState, onPlayButton, onProgramCard, serviceState, playerDataState) }

        composable(
            route = Destinations.audobooks,
        ) { AudioBookView(mainUiState, onPlayButton, onAudioProgramCard, serviceState, playerDataState) }
    }
}