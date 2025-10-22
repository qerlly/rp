package pl.agora.radiopogoda.ui.composables.views.hamburger

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.Job
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.ui.composables.navigation.Destinations
import pl.agora.radiopogoda.ui.theme.black
import pl.agora.radiopogoda.ui.theme.main


@Composable
fun HamburgerView(drawerAction: () -> Job, navController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize()
        .verticalScroll(rememberScrollState())
        .background(main)
    ) {
        HamburgerHeader(drawerAction)
        HorizontalDivider()
        HamburgerContent(navController, drawerAction)
    }
}

enum class HamburgerItem(val stringResId: Int, val destination: String) {
    LIVE_RADIO(R.string.live_radio, Destinations.home),
    MUSIC_CHANNELS(R.string.music_channels, Destinations.channels),
    CONTESTS(R.string.contest, Destinations.contest),
    PODCASTS(R.string.podcast, Destinations.podcast),
    AUDIOBOOKS(R.string.audiobooks, Destinations.audobooks),
    ON_RADIO(R.string.onRadio, Destinations.onRadio),
    NEWS(R.string.news, Destinations.news),
    MUSIC_LIST(R.string.check_playlist, Destinations.musicList),
    INFO(R.string.info, Destinations.contact),
}