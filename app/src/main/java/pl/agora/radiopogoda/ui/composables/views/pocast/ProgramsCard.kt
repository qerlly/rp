package pl.agora.radiopogoda.ui.composables.views.pocast

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.ui.theme.secondText

@Composable
fun ProgramCard(
    onClick: (String) -> Unit,
    imageUrl: String,
    title: String,
    programId: String,
) = Card(
    modifier = Modifier
        .fillMaxSize()
        .clickable { onClick(programId) },
    shape = RoundedCornerShape(8.dp),
    colors = CardColors(
        contentColor = MaterialTheme.colorScheme.onSecondary,
        disabledContentColor = MaterialTheme.colorScheme.onSecondary,
        containerColor = MaterialTheme.colorScheme.secondary,
        disabledContainerColor = MaterialTheme.colorScheme.secondary,
    ),
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val width = remember { maxWidth }
        Column(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = imageUrl,
                    error = painterResource(R.drawable.logo)
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(width)
                    .clip(RoundedCornerShape(8.dp))
            )
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)) {
                Text(
                    text = title.uppercase(),
                    color = secondText,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Left,
                    maxLines = 2,
                    minLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(4.dp),
                    fontSize = 12.sp,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}