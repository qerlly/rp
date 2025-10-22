package pl.agora.radiopogoda.ui.composables.views.onRadio

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import pl.agora.radiopogoda.data.model.swipedInfo.RadioProgramItem
import pl.agora.radiopogoda.ui.theme.secondText
import pl.agora.radiopogoda.ui.theme.white
import pl.agora.radiopogoda.utils.TimeHelper
import pl.agora.radiopogoda.ui.theme.main
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.ui.theme.secondary

@Composable
fun OnRadioSection(
    name: String,
    list: List<RadioProgramItem>,
    activeProgram: RadioProgramItem?,
) = Column(
    modifier = Modifier.fillMaxSize().padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
) {
    val configuration = LocalConfiguration.current
    val cardSize = remember { configuration.screenWidthDp / 3.4 }

    Card(
        colors = CardColors(
            containerColor = secondary,
            contentColor = white,
            disabledContentColor = white,
            disabledContainerColor = secondary
        ),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = name,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = white,
            modifier = Modifier.padding(6.dp)
        )
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        list.forEach { OnRadioCard(it, cardSize, activeProgram) }
    }
}

@Composable
private fun OnRadioCard(
    data: RadioProgramItem,
    size: Double,
    activeProgram: RadioProgramItem?,
) = Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
    Image(
        painter = rememberAsyncImagePainter(
            model = data.program.image.link,
            error = painterResource(R.drawable.logo)
        ),
        contentDescription = null,
        contentScale = ContentScale.FillBounds,
        modifier = Modifier.height(size.dp).width(size.dp).clip(RoundedCornerShape(10.dp))
    )
    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (data == activeProgram) ActualPosition()
            Text(
                text = "${TimeHelper.formatSecondsToTime(data.start)} - ${TimeHelper.formatSecondsToTime(data.end)} ",
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 15.sp
            )
        }

        Spacer(Modifier.padding(3.dp))
        Text(
            text = data.program.name,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontSize = 15.5.sp
        )
        Spacer(Modifier.padding(2.dp))
        Text(
            text = data.people.joinToString("\n") { it.name },
            fontWeight = FontWeight.Medium,
            overflow = TextOverflow.Ellipsis,
            fontSize = 13.5.sp,
            color = secondText
        )
    }
}

@Composable
private fun ActualPosition() = Row(
    horizontalArrangement = Arrangement.spacedBy(4.dp),
    verticalAlignment = Alignment.CenterVertically
){
    Icon(
        painter = painterResource(R.drawable.play_main),
        contentDescription = null,
        modifier = Modifier.size(32.dp),
        tint = Color.Unspecified,
    )
    Text(
        text = stringResource(R.string.actual_on_radio_pos),
        fontWeight = FontWeight.Medium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        fontSize = 15.sp
    )
}