package pl.agora.radiopogoda.ui.composables.customViews.tab

import android.content.Intent
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.data.model.news.NewsModel
import pl.agora.radiopogoda.ui.theme.blackTransparent
import pl.agora.radiopogoda.ui.theme.lightGray
import pl.agora.radiopogoda.ui.theme.white
import pl.agora.radiopogoda.ui.theme.whiteTransparent
import pl.agora.radiopogoda.utils.openUrl

@ExperimentalPagerApi
@Composable
fun AnimatedNewsHeader(list: List<NewsModel>) = Box(Modifier.fillMaxWidth()) {

    val state = rememberPagerState(pageCount = list.size)

    val imageUrl = remember { mutableStateOf("") }

    val context = LocalContext.current

    HorizontalPager(state = state, modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f)) { page ->

        imageUrl.value = list[page].image

        Box(Modifier.fillMaxSize().padding(top = 2.dp)) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = imageUrl.value,
                    error = painterResource(R.drawable.logo)
                ),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                Modifier.fillMaxSize().background(
                    brush = Brush.verticalGradient(listOf(whiteTransparent, blackTransparent))
                ).clickable {  Intent(Intent.ACTION_VIEW, list[page].url.toUri()).openUrl(context) }
            )
            Column(Modifier.fillMaxWidth().align(Alignment.BottomCenter)) {
                Text(
                    text = list[page].title,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    textAlign = TextAlign.Start,
                    fontSize = 16.sp,
                    maxLines = 2,
                    color = white,
                    fontWeight = FontWeight.Bold
                )

                DotsIndicator(
                    totalDots = list.size,
                    selectedIndexProvider = { state.currentPage }
                )
            }
        }
    }

    LaunchedEffect(key1 = state.currentPage) {
        delay(3000)
        var newPosition = state.currentPage + 1
        if (newPosition > list.size - 1) newPosition = 0
        state.animateScrollToPage(
            animationSpec = tween(durationMillis = 0),
            page = newPosition
        )
    }
}

@Composable
private fun DotsIndicator(totalDots: Int, selectedIndexProvider: () -> Int) {
    LazyRow(
        modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        items(totalDots) { index ->
            if (index == selectedIndexProvider()) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color = white)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(color = lightGray)
                )
            }

            if (index != totalDots - 1) {
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
            }
        }
    }
}