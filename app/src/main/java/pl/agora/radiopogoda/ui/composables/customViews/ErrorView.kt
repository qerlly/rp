package pl.agora.radiopogoda.ui.composables.customViews

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.agora.radiopogoda.R

@Composable
fun ErrorView(text: String, modifier: Modifier = Modifier) = Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier,
) {
    Icon(
        painter = painterResource(R.drawable.ic_baseline_error),
        contentDescription = null,
        modifier = Modifier.size(64.dp),
        tint = MaterialTheme.colorScheme.primaryContainer,
    )
    Text(
        text = stringResource(R.string.error_view_title),
        color = MaterialTheme.colorScheme.onPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
    )
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onPrimary,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Light,
        fontSize = 15.sp,
    )
}