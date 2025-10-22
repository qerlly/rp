package pl.agora.radiopogoda.ui.composables.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import pl.agora.radiopogoda.data.model.channel.Channel
import pl.agora.radiopogoda.data.wrappers.PlayerMediaItemModel
import pl.agora.radiopogoda.infrastructure.services.music.MusicServiceActions
import pl.agora.radiopogoda.ui.composables.customViews.SnackBar
import pl.agora.radiopogoda.ui.composables.customViews.appBar.MainAppBar
import pl.agora.radiopogoda.ui.composables.customViews.appBar.MainBottomBar
import pl.agora.radiopogoda.ui.composables.customViews.drawer.DrawerContent
import pl.agora.radiopogoda.ui.composables.customViews.player.BottomPlayerView
import pl.agora.radiopogoda.ui.composables.navigation.Destinations
import pl.agora.radiopogoda.ui.composables.navigation.Navigation
import pl.agora.radiopogoda.ui.theme.white
import pl.agora.radiopogoda.ui.uiData.BottomSheetState
import pl.agora.radiopogoda.ui.viewModels.MainViewModel
import pl.agora.radiopogoda.ui.viewModels.SnackbarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onNewAudienceEvent: (String) -> Unit,
    resetConsent: () -> Unit,
    showAd: (() -> Unit) -> Job,
    mainViewModel: MainViewModel,
) = Box(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {

    val navController = rememberNavController()
    DestinationTracker(navController, onNewAudienceEvent)

    val scope = rememberCoroutineScope()
    val containerColor = remember { mutableStateOf(false) }

    val snackbarViewModel = hiltViewModel<SnackbarViewModel>()
    val snackbarMessageState = snackbarViewModel.snackbarMessages.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val saveCity = remember { { value: String -> mainViewModel.saveCity(value) } }

    val serviceState = mainViewModel.serviceState.collectAsStateWithLifecycle()
    val playerDataState = mainViewModel.playerMediaData.collectAsStateWithLifecycle()
    val playerPositionState = mainViewModel.playerPositionState.collectAsStateWithLifecycle()

    val mainUiState = mainViewModel.mainUiState.collectAsStateWithLifecycle()
    val showBottomSheet = remember { mutableStateOf(BottomSheetState.NONE) }

    val onProgramCard = remember { { id: String -> mainViewModel.onProgramCard(id) } }
    val onAudioProgramCard = remember { { id: String -> mainViewModel.onAudioProgramCard(id) } }

    val openBottomSheet = remember {
        { state: BottomSheetState ->
            containerColor.value = state.name == BottomSheetState.PLAYER_VIEW.name
            onNewAudienceEvent(state.name)
            scope.launch {
                showBottomSheet.value = state
                bottomSheetState.expand()
            }
        }
    }

    val closeBottomSheet = remember {
        {
            scope.launch {
                showBottomSheet.value = BottomSheetState.NONE.also {
                    bottomSheetState.hide()
                    containerColor.value = false
                }
            }
        }
    }

    val onPlayButton = remember { { data: PlayerMediaItemModel -> mainViewModel.onPlayButton(data) } }
    val onChannelCard = remember { { channel: Channel -> mainViewModel.onChannelCard(channel) } }
    val onSeek = remember { { action: MusicServiceActions, value: Float? -> mainViewModel.onSeek(action, value) } }
    val onSeekEnded = remember { { mainViewModel.onSeekEnded() } }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) {
                snackbarMessageState.value?.let { SnackBar(it) }
            }
        },
        topBar = { MainAppBar(navController) },
        bottomBar = { MainBottomBar(navController, openBottomSheet, showAd) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {

            Navigation(
                navController = navController,
                resetConsent = resetConsent,
                mainUiState = mainUiState,
                serviceState = serviceState,
                onChannelCard = onChannelCard,
                onPlayButton = onPlayButton,
                playerDataState = playerDataState,
                onProgramCard = onProgramCard,
                saveCity = saveCity,
                onAudioProgramCard = onAudioProgramCard
            )

            BottomPlayerView(
                modifier = Modifier.fillMaxWidth().height(64.dp).align(Alignment.BottomCenter),
                drawerAction = openBottomSheet,
                playerDataState = playerDataState,
                mainUiState = mainUiState,
                serviceState = serviceState,
                onPlayButton = onPlayButton,
            )

            if (showBottomSheet.value != BottomSheetState.NONE) {
                ModalBottomSheet(
                    shape = RectangleShape,
                    sheetState = bottomSheetState,
                    modifier = Modifier.fillMaxSize(),
                    containerColor = if (containerColor.value) MaterialTheme.colorScheme.primaryContainer else white,
                    onDismissRequest = { showBottomSheet.value = BottomSheetState.NONE },
                    dragHandle = { BottomSheetDefaults.DragHandle(color = if (containerColor.value) MaterialTheme.colorScheme.primaryContainer else white) }
                ) {
                    DrawerContent(
                        navController = navController,
                        bottomSheetState = showBottomSheet,
                        closeDrawer = closeBottomSheet,
                        serviceState = serviceState,
                        playerDataState = playerDataState,
                        playerPositionState = playerPositionState,
                        mainUiState = mainUiState,
                        onPlayButton = onPlayButton,
                        onChannelCard = onChannelCard,
                        onSeek = onSeek,
                        onSeekEnded = onSeekEnded,
                    )
                }
            }

            LaunchedEffect(snackbarMessageState.value) {
                snackbarMessageState.value?.let {
                    snackbarHostState.showSnackbar(message = "", duration = it.duration)
                }
                snackbarViewModel.removeMessage()
            }
        }
    }
}

@Composable
private fun DestinationTracker(navController: NavController, onNewAudienceEvent: (String) -> Unit) {
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow
            .map { backStackEntry ->
                val routePattern = backStackEntry.destination.route

                val params: Map<String, String> = backStackEntry.arguments
                    ?.keySet()
                    ?.associateWith { key ->
                        backStackEntry.arguments?.getString(key).orEmpty()
                    }
                    ?: emptyMap()

                val filledRoute: String = routePattern
                    ?.let { pattern ->
                        params.entries.fold(pattern) { acc, (key, value) ->
                            acc.replace("{$key}", value)
                        }
                    }
                    ?: backStackEntry.destination.id.toString()

                filledRoute
            }
            .distinctUntilChanged()
            .filterNot { it == Destinations.news }
            .collect { screenWithArgs -> onNewAudienceEvent(screenWithArgs) }
    }
}