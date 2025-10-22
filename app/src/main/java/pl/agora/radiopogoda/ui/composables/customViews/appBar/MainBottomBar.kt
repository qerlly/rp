package pl.agora.radiopogoda.ui.composables.customViews.appBar

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.Job
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.ui.composables.navigation.Destinations
import pl.agora.radiopogoda.ui.theme.secondary
import pl.agora.radiopogoda.ui.uiData.BottomSheetState
import pl.agora.radiopogoda.utils.openDestination

@Composable
fun MainBottomBar(
    navController: NavHostController,
    drawerAction: (BottomSheetState) -> Job,
    showAd: (() -> Unit) -> Job
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        tonalElevation = 4.dp
    ) {
        val navState by navController.currentBackStackEntryFlow.collectAsStateWithLifecycle(null)

        val currentRoute = navState?.destination?.route

        NavigationItem.entries.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        modifier = Modifier.size(item.size.dp).padding(top = 4.dp),
                        painter = painterResource(item.icon),
                        contentDescription = stringResource(item.title),
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = secondary,
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = secondary,
                    unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    indicatorColor = Color.Transparent
                ),
                label = {
                    Text(
                        text = stringResource(item.title),
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 9.sp
                    )
                },
                onClick = {
                    if (item.route == Destinations.hamburger)  {
                        drawerAction(BottomSheetState.MORE_VIEW)
                    } else {
                        showAd { navController.openDestination(item.route) }
                    }
                },
                alwaysShowLabel = true,
                selected = currentRoute != null && currentRoute.contains(item.route),
                modifier = Modifier.wrapContentHeight()
            )
        }
    }
}

enum class NavigationItem(val route: String, val icon: Int, val title: Int, val size: Int) {
    ON_RADIO(Destinations.onRadio, R.drawable.on_radio, R.string.onRadio, 32),
    AUDIOBOOKS(Destinations.audobooks, R.drawable.ic_audiobooks, R.string.audiobooks, 32),
    HOME(Destinations.home, R.drawable.ic_home, R.string.home, 32),
    PODCASTS(Destinations.podcast, R.drawable.ic_podcast, R.string.podcast, 32),
    HAMBURGER(Destinations.hamburger, R.drawable.ic_menu, R.string.menu, 32),
}