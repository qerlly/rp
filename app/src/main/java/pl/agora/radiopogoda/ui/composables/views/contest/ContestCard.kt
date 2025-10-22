package pl.agora.radiopogoda.ui.composables.views.contest

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import pl.agora.radiopogoda.ui.theme.white
import pl.agora.radiopogoda.utils.openUrl
import androidx.core.net.toUri
import pl.agora.radiopogoda.R

@ExperimentalMaterialApi
@Composable
fun ContestCard(link: String, url: String, context: Context) = Card(
    shape = RoundedCornerShape(4.dp),
    backgroundColor = white,
    elevation = 8.dp,
    modifier = Modifier.fillMaxSize(),
    onClick = { Intent(Intent.ACTION_VIEW, url.toUri()).openUrl(context) }
) {
    Image(
        painter = rememberAsyncImagePainter(
            model = link,
            error = painterResource(R.drawable.logo)
        ),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}