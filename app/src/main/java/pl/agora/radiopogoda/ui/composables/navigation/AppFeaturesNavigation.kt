package pl.agora.radiopogoda.ui.composables.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Job
import pl.agora.radiopogoda.ui.composables.views.MainScreen
import pl.agora.radiopogoda.ui.viewModels.MainViewModel
import androidx.navigation.compose.composable

@Composable
fun AppFeaturesNavigation(
    onNewAudienceEvent: (String) -> Unit,
    resetConsent: () -> Unit,
    showAd: (() -> Unit) -> Job,
    mainViewModel: MainViewModel,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destinations.main
    ) {
        composable(Destinations.main) {
            MainScreen(
                onNewAudienceEvent = onNewAudienceEvent,
                resetConsent = resetConsent,
                showAd = showAd,
                mainViewModel = mainViewModel,
            )
        }
    }
}