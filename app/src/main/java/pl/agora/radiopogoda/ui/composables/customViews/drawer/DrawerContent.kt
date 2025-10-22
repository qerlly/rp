package pl.agora.radiopogoda.ui.composables.customViews.drawer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import kotlinx.coroutines.Job
import pl.agora.radiopogoda.data.model.channel.Channel
import pl.agora.radiopogoda.data.wrappers.PlayerMediaItemModel
import pl.agora.radiopogoda.infrastructure.services.music.MusicServiceActions
import pl.agora.radiopogoda.infrastructure.services.music.MusicServiceState
import pl.agora.radiopogoda.ui.composables.views.hamburger.HamburgerView
import pl.agora.radiopogoda.ui.composables.views.player.PlayerView
import pl.agora.radiopogoda.ui.uiData.BottomSheetState
import pl.agora.radiopogoda.ui.uiData.MainUiState

@Composable
fun DrawerContent(
    navController: NavHostController,
    bottomSheetState: State<BottomSheetState>,
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
    when (bottomSheetState.value) {
        BottomSheetState.MORE_VIEW -> HamburgerView(closeDrawer, navController)
        BottomSheetState.PLAYER_VIEW -> PlayerView(
            closeDrawer = closeDrawer,
            playerDataState = playerDataState,
            serviceState = serviceState,
            playerPositionState = playerPositionState,
            mainUiState = mainUiState,
            onPlayButton = onPlayButton,
            onChannelCard = onChannelCard,
            onSeekEnded = onSeekEnded,
            onSeek = onSeek,
        )
        BottomSheetState.NONE -> Box(Modifier.fillMaxWidth())
    }
}