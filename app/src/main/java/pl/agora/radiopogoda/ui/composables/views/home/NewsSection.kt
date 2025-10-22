package pl.agora.radiopogoda.ui.composables.views.home

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.flow.StateFlow
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.data.api.ApiResult
import pl.agora.radiopogoda.data.model.news.NewsModel
import pl.agora.radiopogoda.ui.composables.customViews.ErrorView
import pl.agora.radiopogoda.ui.composables.navigation.Destinations
import pl.agora.radiopogoda.utils.openDestination
import pl.agora.radiopogoda.utils.openUrl

@Composable
fun NewsSection(
    navController: NavHostController,
    getNewsState: () -> StateFlow<ApiResult<List<NewsModel>>>
) {
    HomeSection(
        text = stringResource(R.string.news),
        onClick = { navController.openDestination(Destinations.news) }
    ) {
        val newsState = getNewsState().collectAsStateWithLifecycle()

        val configuration = LocalConfiguration.current
        val loadingModifier = remember {
            Modifier.fillMaxHeight().width(configuration.screenWidthDp.dp)
                .defaultMinSize(minHeight = (configuration.screenHeightDp / 3.4).dp)
        }

        when (val news = newsState.value) {
            is ApiResult.Loading ->
                Box(loadingModifier) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primaryContainer
                    )
                }

            is ApiResult.Failure ->
                Box(Modifier.fillMaxHeight().width(configuration.screenWidthDp.dp)) {
                    ErrorView(
                        text = stringResource(R.string.retrofit_error),
                        modifier = Modifier.width(250.dp).align(Alignment.Center),
                    )
                }

            is ApiResult.Success -> {
                val list = remember { news.value.take(20) }

                val context = LocalContext.current

                LazyRow(modifier = Modifier.fillMaxHeight(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    item { Spacer(Modifier.padding(2.dp)) }
                    items(count = list.size) {
                        Box(Modifier.width(300.dp).aspectRatio(4f / 3f)) {
                            SliderCard(
                                modifier = Modifier.fillMaxSize()
                                    .clickable {
                                        Intent(Intent.ACTION_VIEW, list[it].url.toUri()).openUrl(context)
                                    },
                                imageUrl = list[it].image,
                                title = list[it].title,
                                upperTitle = list[it].parent ?: "",
                                newsCategory = true
                            )
                        }
                    }
                    item { Spacer(Modifier.padding(2.dp)) }
                }
            }
        }
    }
}