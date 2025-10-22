package pl.agora.radiopogoda.ui.composables.views.contact

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.utils.Consts
import pl.agora.radiopogoda.utils.openUrl

@Composable
fun SocialRow() = Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
) {

    val context = LocalContext.current

    Text(
        text = stringResource(R.string.search_us),
        color = MaterialTheme.colorScheme.onPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
    )
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top = 12.dp)) {
        ContactIcon(
            onClick = { Intent(Intent.ACTION_VIEW, Consts.INSTAGRAM.toUri()).openUrl(context) },
            painter = painterResource(R.drawable.ic_instagram),
        )
        ContactIcon(
            onClick = { Intent(Intent.ACTION_VIEW, Consts.FACEBOOK.toUri()).openUrl(context) },
            painter = painterResource(R.drawable.ic_facebook)
        )
        ContactIcon(
            onClick = { Intent(Intent.ACTION_VIEW, Consts.SITE.toUri()).openUrl(context) },
            painter = painterResource(R.drawable.ic_baseline_language_24)
        )
        ContactIcon(
            onClick = { Intent(Intent.ACTION_VIEW, Consts.TIKTOK.toUri()).openUrl(context) },
            painter = painterResource(R.drawable.tik_tok)
        )
    }
}

@Composable
private fun ContactIcon(onClick: () -> Unit, painter: Painter) = IconButton(onClick = { onClick() }) {
    Icon(painter = painter, contentDescription = null, modifier = Modifier.size(28.dp), tint = MaterialTheme.colorScheme.onPrimary)
}