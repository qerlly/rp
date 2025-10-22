package pl.agora.radiopogoda.ui.composables.views.contact

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.utils.Frequencies

@Composable
fun FrequenciesColumn() = Column(
    modifier = Modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Text(
        text = stringResource(R.string.frequencies),
        color = MaterialTheme.colorScheme.onPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        modifier = Modifier.padding(bottom = 16.dp)
    )
    Row {
        Column(Modifier.fillMaxWidth(0.5f).padding(start = 24.dp).animateContentSize()) {
            Frequencies.getLeftColumn().forEach {
                Text(text = "${it.city} ${it.frequency}", color = MaterialTheme.colorScheme.onPrimary, fontSize = 12.5.sp)
            }
        }
        Column(Modifier.fillMaxWidth().padding(start = 16.dp)) {
            Frequencies.getRightColumn().forEach {
                Text(text = "${it.city} ${it.frequency}", color = MaterialTheme.colorScheme.onPrimary, fontSize = 12.5.sp)
            }
        }
    }
}