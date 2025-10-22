package pl.agora.radiopogoda.ui.composables.customViews.appBar

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.TopAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import pl.agora.radiopogoda.ui.composables.navigation.Destinations
import pl.agora.radiopogoda.ui.theme.main
import pl.agora.radiopogoda.ui.theme.transparent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.ui.theme.black

@Composable
fun MainAppBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBackButton = remember(currentRoute) {
        derivedStateOf {
            currentRoute != null && (
                    currentRoute in setOf(
                        Destinations.channels,
                        Destinations.musicList,
                        Destinations.contact,
                        Destinations.cities,
                    )
            )
        }
    }

    val appBarResource = remember(currentRoute) { appBarResourceIds(currentRoute) }
    val (title, iconRes) = appBarResource

    Column(
        modifier = Modifier.fillMaxWidth().fillMaxWidth().background(main)
    ) {
        TopAppBar(backgroundColor = main) {
            AppBarContent(
                title = title,
                iconRes = iconRes,
                showBackButton = showBackButton,
                onBack = { navController.popBackStack() },
            )
        }
    }
}

@Composable
private fun AppBarContent(
    title: Int?,
    @DrawableRes iconRes: Int?,
    showBackButton: State<Boolean>,
    onBack: () -> Unit,
) {
    if (title != null) {
        ScreensAppBar(title, iconRes, showBackButton, onBack)
    } else {
        HomeAppBar()
    }
}

@Composable
private fun HomeAppBar() = Row(
    modifier = Modifier.fillMaxSize().background(main).padding(horizontal = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.Center
) {
    Icon(
        painter = painterResource(R.drawable.ic_logo),
        contentDescription = null,
        tint = Color.Unspecified,
        modifier = Modifier.fillMaxHeight().padding(start = 4.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreensAppBar(
    @StringRes titleRes: Int,
    @DrawableRes iconRes: Int?,
    showBackButton: State<Boolean>,
    onBack: () -> Unit,
) {
    val colors = TopAppBarDefaults.topAppBarColors(
        navigationIconContentColor = black,
        titleContentColor = black,
        containerColor = transparent
    )

    Row(Modifier.fillMaxWidth().background(main)) {
        CenterAlignedTopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = stringResource(titleRes),
                        fontWeight = FontWeight.Light,
                        fontSize = 18.sp
                    )
                    iconRes?.let {
                        Icon(
                            painter = painterResource(it),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.fillMaxHeight()
                        )
                    }
                }
            },
            navigationIcon = {
                if (showBackButton.value) {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = null
                        )
                    }
                }
            },
            colors = colors
        )
    }
}

private fun appBarResourceIds(route: String?): Pair<Int?, Int?> = when (route) {
    Destinations.home -> null to R.drawable.ic_logo
    Destinations.news -> R.string.news to R.drawable.ic_logo
    Destinations.podcast -> R.string.podcast to R.drawable.ic_logo
    Destinations.channels -> R.string.music_channels to R.drawable.ic_logo
    Destinations.charts -> R.string.charts to R.drawable.ic_logo
    Destinations.contact -> R.string.info to R.drawable.ic_logo
    Destinations.musicList -> R.string.playlist_title to R.drawable.ic_logo
    Destinations.onRadio -> R.string.onRadio to R.drawable.ic_logo
    Destinations.audobooks -> R.string.audiobooks to R.drawable.ic_logo
    Destinations.contest -> R.string.contest to R.drawable.ic_logo
    Destinations.cities -> R.string.pick_city to null
    else -> if (route?.contains(Destinations.news) == true)
        R.string.news to R.drawable.ic_logo
    else
        null to null
}
