package pl.agora.radiopogoda.ui.composables.customViews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.agora.radiopogoda.infrastructure.snackbar.SnackbarMessage

@Composable
fun SnackBar(message: SnackbarMessage) = Snackbar(
    modifier = Modifier.padding(16.dp),
    containerColor = MaterialTheme.colorScheme.primary,
) {
    val title = stringResource(message.titleId)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        content = {
            Icon(
                painter = painterResource(message.drawableId),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(42.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween,
                content = {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = stringResource(message.descriptionId),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Justify,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            )
        }
    )
}