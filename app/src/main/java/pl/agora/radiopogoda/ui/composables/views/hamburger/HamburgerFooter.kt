package pl.agora.radiopogoda.ui.composables.views.hamburger

import android.content.Intent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.ui.theme.black
import pl.agora.radiopogoda.utils.Consts.FACEBOOK
import pl.agora.radiopogoda.utils.Consts.INSTAGRAM
import pl.agora.radiopogoda.utils.Consts.SITE
import pl.agora.radiopogoda.utils.Consts.TIKTOK
import pl.agora.radiopogoda.utils.openUrl

@Composable
fun HamburgerFooter() {
    val context = LocalContext.current

    val onClick = remember {
        { url: String ->
            Intent(Intent.ACTION_VIEW, url.toUri()).openUrl(context)
        }
    }

    Row {
        FooterIcon(
            onClick = onClick,
            painterId = R.drawable.ic_instagram,
            url = INSTAGRAM
        )
        FooterIcon(
            onClick = onClick,
            painterId = R.drawable.ic_facebook,
            url = FACEBOOK
        )
        FooterIcon(
            onClick = onClick,
            painterId = R.drawable.ic_baseline_language_24,
            url = SITE
        )
        FooterIcon(
            onClick = onClick,
            painterId = R.drawable.tik_tok,
            url = TIKTOK
        )
    }
}

@Composable
private fun FooterIcon(onClick: (String) -> Unit, painterId: Int, url: String) {
    IconButton(onClick = { onClick(url) }) {
        Icon(
            painter = painterResource(painterId),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = black
        )
    }
}