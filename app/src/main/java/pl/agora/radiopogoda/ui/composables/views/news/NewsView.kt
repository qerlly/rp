package pl.agora.radiopogoda.ui.composables.views.news

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import pl.agora.radiopogoda.data.api.ApiResult
import pl.agora.radiopogoda.data.model.news.NewsModel
import pl.agora.radiopogoda.ui.composables.customViews.ErrorView
import pl.agora.radiopogoda.ui.composables.customViews.tab.CustomScrollableTabRow
import pl.agora.radiopogoda.ui.composables.navigation.TabItem
import pl.agora.radiopogoda.ui.theme.main
import pl.agora.radiopogoda.utils.Consts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import pl.agora.radiopogoda.R

@ExperimentalPagerApi
@Composable
fun NewsView(
    onTabChanged: (Int) -> Unit,
    getNewsState: (String) -> StateFlow<ApiResult<List<NewsModel>>>
) {
    val tabs = TabItem.getNewsTabs()
    val pagerState = rememberPagerState(pageCount = tabs.size)

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page -> onTabChanged(page) }
    }

    Column(Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxWidth().background(main)) {
            CustomScrollableTabRow(pagerState, tabs)
        }
        HorizontalPager(state = pagerState) { page ->
            TabContainer(tabs[page].url, getNewsState)
        }
    }
}

@ExperimentalPagerApi
@Composable
fun TabContainer(url: String, getNewsState: (String) -> StateFlow<ApiResult<List<NewsModel>>>) =
    Box(Modifier.fillMaxSize()) {
        val state = getNewsState(url).collectAsStateWithLifecycle()
        when (val data = state.value) {
            is ApiResult.Loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primaryContainer
            )

            is ApiResult.Failure ->
                ErrorView(
                    text = stringResource(R.string.retrofit_error),
                    modifier = Modifier.width(250.dp).align(Alignment.Center),
                )

            is ApiResult.Success -> {
                NewsGrid(
                    data = data.value,
                    showAnimatedHeader = url == Consts.ALL_NEWS_URL,
                )
            }
        }
    }