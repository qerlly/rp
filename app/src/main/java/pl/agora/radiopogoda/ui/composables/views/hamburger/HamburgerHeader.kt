package pl.agora.radiopogoda.ui.composables.views.hamburger

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.ui.theme.black

@Composable
fun HamburgerHeader(drawerAction: () -> Job) = Row(
    modifier = Modifier.fillMaxWidth().height(80.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
) {
    Icon(
        painter = painterResource(R.drawable.ic_logo),
        contentDescription = null,
        tint = Color.Unspecified,
        modifier = Modifier.padding(start = 16.dp)
    )
    IconButton(onClick = { drawerAction() }) {
        Icon(painter = painterResource(R.drawable.ic_close),
            contentDescription = null,
            tint = black,
            modifier = Modifier.fillMaxHeight().padding(end = 16.dp)
        )
    }
}