package pl.agora.radiopogoda.ui.composables.customViews.tab

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.launch
import pl.agora.radiopogoda.ui.composables.navigation.TabItem
import pl.agora.radiopogoda.ui.theme.black
import pl.agora.radiopogoda.ui.theme.secondary
import pl.agora.radiopogoda.ui.theme.transparent
import pl.agora.radiopogoda.ui.theme.white

@Composable
fun CustomTabRow(
    currentPageProvider: () -> Int,
    onClick: (Int) -> Unit,
    list: Array<TabItem>,
) {
    TabRow(
        selectedTabIndex = currentPageProvider(),
        indicator = { tabPositions ->
            TabRowDefaults.PrimaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[currentPageProvider()]),
                color = white,
                width = 100.dp
            )
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = white
    ) {
        list.forEachIndexed { index, tab ->
            Tab(
                selected = currentPageProvider() == index,
                onClick = { onClick(index) },
                text = { Text(text = tab.tabName) }
            )
        }
    }
}

@ExperimentalPagerApi
@Composable
fun CustomScrollableTabRow(
    pagerState: PagerState,
    list: Array<TabItem>,
) {
    val pagerStateValue = pagerState.currentPage
    val scope = rememberCoroutineScope()

    val density = LocalDensity.current
    val tabWidths = remember {
        val tabWidthStateList = mutableStateListOf<Dp>()
        repeat(list.size) { tabWidthStateList.add(0.dp) }
        tabWidthStateList
    }

    ScrollableTabRow(
        selectedTabIndex = pagerStateValue,
        contentColor = white,
        edgePadding = 0.dp,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.customTabIndicatorOffset(
                    currentTabPosition = tabPositions[pagerStateValue],
                    tabWidth = tabWidths[pagerStateValue]
                ),
                color = secondary
            )
        },
        containerColor = transparent
    ) {
        list.forEachIndexed { tabIndex, tab ->
            Tab(
                selected = pagerStateValue == tabIndex,
                onClick = { scope.launch { pagerState.scrollToPage(tabIndex) } },
                text = {
                    Text(
                        text = tab.tabName,
                        color = black,
                        onTextLayout = { textLayoutResult ->
                            tabWidths[tabIndex] = with(density) { textLayoutResult.size.width.toDp() }
                        }
                    )
                }
            )
        }
    }
}

fun Modifier.customTabIndicatorOffset(
    currentTabPosition: TabPosition,
    tabWidth: Dp
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "customTabIndicatorOffset"
        value = currentTabPosition
    }
) {
    val currentTabWidth by animateDpAsState(
        targetValue = tabWidth,
        animationSpec = tween(durationMillis = 5, easing = FastOutSlowInEasing)
    )
    val indicatorOffset by animateDpAsState(
        targetValue = ((currentTabPosition.left + currentTabPosition.right - tabWidth) / 2),
        animationSpec = tween(durationMillis = 5, easing = FastOutSlowInEasing)
    )
    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = indicatorOffset)
        .width(currentTabWidth)
}

