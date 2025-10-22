package pl.agora.radiopogoda.ui.composables.views.hamburger

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.Job
import pl.agora.radiopogoda.utils.openDestination

@Composable
fun HamburgerContent(
    navController: NavHostController,
    drawerAction: () -> Job,
) = Column(
    modifier = Modifier.padding(start = 20.dp, end = 20.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
) {

    val onMenuItemClick = remember {
        { destination: String -> navController.openDestination(destination).also { drawerAction() } }
    }

    HamburgerItem.entries.forEach {
        SingleItem(it.stringResId, onMenuItemClick, it.destination)
    }
    HamburgerFooter()
}