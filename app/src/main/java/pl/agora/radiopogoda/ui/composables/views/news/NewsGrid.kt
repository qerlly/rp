package pl.agora.radiopogoda.ui.composables.views.news

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.data.model.news.NewsModel
import pl.agora.radiopogoda.ui.advert.AdvertViewBig
import pl.agora.radiopogoda.ui.composables.customViews.tab.AnimatedNewsHeader
import pl.agora.radiopogoda.utils.Consts
import pl.agora.radiopogoda.utils.openUrl

@ExperimentalPagerApi
@Composable
fun NewsGrid(data: List<NewsModel>, showAnimatedHeader: Boolean) {
    val context = LocalContext.current

    val list = data
    val news = remember { mutableStateOf(list) }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (showAnimatedHeader)
            item(
                span = { GridItemSpan(maxCurrentLineSpan) },
                key = "AnimatedHeader"
            ) {
                AnimatedNewsHeader(list.take(Consts.ANIMATED_SLIDER_CARD_SIZE))
            }.also { news.value = list.drop(Consts.ANIMATED_SLIDER_CARD_SIZE) }
        else
            item(
                span = { GridItemSpan(maxCurrentLineSpan) },
                key = "Spacer"
            ) { Spacer(Modifier.padding(6.dp)) }

        news.value.forEachIndexed { index, data ->
            if (index == 6)
                item(
                    span = { GridItemSpan(maxCurrentLineSpan) },
                    key = "AdvertViewBig"
                ) { AdvertViewBig() }

            item(
                span = { GridItemSpan(1) },
                key = "${data.url} + ${data.title}"
            ) {
                NewsCard(
                    title = data.title,
                    imageUrl = data.image,
                    leftSide = index % 2 == 0,
                    onClick = { Intent(Intent.ACTION_VIEW, data.url.toUri()).openUrl(context) },
                )
            }
        }
        repeat(if (news.value.size % 2 == 0) 1 else 2) {
            item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                Spacer(Modifier.height(64.dp))
            }
        }
    }
}


@Composable
private fun NewsCard(
    title: String,
    imageUrl: String,
    leftSide: Boolean,
    onClick: () -> Unit,
) = Card(
    shape = RoundedCornerShape(8.dp),
    modifier = if (leftSide) Modifier.padding(start = 12.dp) else Modifier.padding(end = 12.dp),
    colors = CardColors(
        contentColor = MaterialTheme.colorScheme.onSecondary,
        disabledContentColor = MaterialTheme.colorScheme.onSecondary,
        containerColor = MaterialTheme.colorScheme.secondary,
        disabledContainerColor = MaterialTheme.colorScheme.secondary,
    ),
) {
    BoxWithConstraints(Modifier.fillMaxSize().clickable { onClick() }) {
        val width = maxWidth
        Column(Modifier.fillMaxWidth()) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = imageUrl,
                    error = painterResource(R.drawable.logo)
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(width).clip(RoundedCornerShape(8.dp))
            )
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSecondary,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Left,
                maxLines = 3,
                lineHeight = 15.sp,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(6.dp).height(50.dp),
                fontSize = 11.sp,
            )
        }
    }
}