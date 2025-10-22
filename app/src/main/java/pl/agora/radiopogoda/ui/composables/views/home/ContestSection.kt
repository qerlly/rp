package pl.agora.radiopogoda.ui.composables.views.home

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import pl.agora.radiopogoda.data.api.ApiResult
import pl.agora.radiopogoda.ui.composables.customViews.ErrorView
import pl.agora.radiopogoda.ui.composables.navigation.Destinations
import pl.agora.radiopogoda.ui.uiData.MainUiState
import pl.agora.radiopogoda.utils.openDestination
import pl.agora.radiopogoda.utils.openUrl
import androidx.core.net.toUri
import pl.agora.radiopogoda.R

@Composable
fun ContestSection(navController: NavHostController, mainUiState: State<MainUiState>) {
    HomeSection(
        text = stringResource(R.string.contest),
        onClick = { navController.openDestination(Destinations.contest) }
    ) {
        val contestState = mainUiState.value.contests

        val configuration = LocalConfiguration.current
        val loadingModifier = remember {
            Modifier.fillMaxHeight().width(configuration.screenWidthDp.dp)
                .defaultMinSize(minHeight = (configuration.screenHeightDp / 3.4).dp)
        }
        val context = LocalContext.current

        when (contestState) {
            is ApiResult.Loading ->
                Box(loadingModifier) { CircularProgressIndicator(Modifier.align(Alignment.Center)) }

            is ApiResult.Failure ->
                Box(Modifier.fillMaxHeight().width(configuration.screenWidthDp.dp)) {
                    ErrorView(
                        text = stringResource(R.string.retrofit_error),
                        modifier = Modifier.width(250.dp).align(Alignment.Center),
                    )
                }

            is ApiResult.Success -> {
                val list = contestState.value.contests

                LazyRow(modifier = Modifier.fillMaxHeight().height(200.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    item { Spacer(Modifier.padding(2.dp)) }
                    items(
                        count = list.size,
                    ) {
                        Box(
                            Modifier.aspectRatio(16f / 9f).width(300.dp).padding(end = 8.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { Intent(Intent.ACTION_VIEW, list[it].link.toUri()).openUrl(context) }
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    model = list[it].url,
                                    error = painterResource(R.drawable.logo)
                                ),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}