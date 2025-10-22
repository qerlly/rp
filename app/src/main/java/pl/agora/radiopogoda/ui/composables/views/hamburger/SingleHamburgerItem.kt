package pl.agora.radiopogoda.ui.composables.views.hamburger

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.agora.radiopogoda.ui.theme.black
import pl.agora.radiopogoda.ui.theme.main

@Composable
fun SingleItem(text: Int, onClick: (String) -> Unit, destination: String) = Card(
    modifier = Modifier.fillMaxSize().padding(start = 4.dp).height(42.dp),
    colors = CardColors(
       containerColor = main,
        disabledContentColor = black,
        contentColor = main,
        disabledContainerColor = black,
    ),
    shape = RoundedCornerShape(12.dp),
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(start = 4.dp).clickable { onClick(destination) },
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(text).uppercase(),
                color = black,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
    }
}