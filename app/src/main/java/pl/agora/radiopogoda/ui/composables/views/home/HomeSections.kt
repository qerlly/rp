package pl.agora.radiopogoda.ui.composables.views.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.ui.theme.secondary

@Composable
fun HomeSection(
    text: String,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) = Column(Modifier.fillMaxWidth().padding(top = 8.dp)) {
    SectionTitle(text) { onClick() }
    Spacer(Modifier.padding(8.dp))
    content()
    Spacer(Modifier.padding(8.dp))
}

@Composable
fun SectionTitle(
    text: String,
    buttonTitle: String = stringResource(R.string.more),
    onClick: () -> Unit
) = Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSecondary,
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(0.7f)
        )
    }
    Text(
        text = buttonTitle,
        color = secondary,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        modifier = Modifier.clickable { onClick() }.padding(end = 12.dp)
    )
}

@Composable
fun SliderCard(
    modifier: Modifier = Modifier,
    imageUrl: String,
    upperTitle: String? = null,
    title: String,
    newsCategory: Boolean = false
) = Card(
    modifier = modifier,
    shape = RoundedCornerShape(8.dp),
    colors = CardColors(
        contentColor = MaterialTheme.colorScheme.onSecondary,
        disabledContentColor = MaterialTheme.colorScheme.onSecondary,
        containerColor = MaterialTheme.colorScheme.secondary,
        disabledContainerColor = MaterialTheme.colorScheme.secondary,
    ),
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val imageModifier = remember {
            if (!newsCategory)
                Modifier.size(maxWidth).clip(RoundedCornerShape(8.dp))
            else
                Modifier.width(maxWidth).height(maxHeight - 60.0.dp).clip(RoundedCornerShape(8.dp))
        }

        Column(Modifier.fillMaxWidth()) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = imageUrl,
                    error = painterResource(R.drawable.logo)
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = imageModifier
            )
            if (upperTitle != null) {
                Text(
                    text = upperTitle.uppercase(),
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Left,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 6.sp,
                    modifier = Modifier.padding(start = 6.dp, top = 4.dp),
                    fontSize = 11.5.sp,
                )
            }
            Box(Modifier.fillMaxHeight()) {
                val alignment: Alignment =
                    if (upperTitle == null) Alignment.CenterStart else Alignment.TopStart
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSecondary,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Left,
                    lineHeight = 15.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(4.dp).align(alignment),
                    fontSize = 11.sp,
                )
            }
        }
    }
}