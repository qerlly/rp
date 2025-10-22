package pl.agora.radiopogoda.ui.composables.views.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.data.model.rds.NextSong
import pl.agora.radiopogoda.data.wrappers.PlayerMediaItemModel
import pl.agora.radiopogoda.infrastructure.services.music.MusicServiceState
import pl.agora.radiopogoda.ui.uiData.MainUiState
import pl.agora.radiopogoda.utils.onShareButton

@Composable
fun PlayerChannelsTopCardSection(
    serviceState: State<MusicServiceState>,
    playerData: PlayerMediaItemModel,
    onPlayButton: (PlayerMediaItemModel) -> Unit,
    mainUiState: State<MainUiState>,
) = Column(Modifier.fillMaxSize().padding(8.dp)) {

    val context = LocalContext.current

    val activeRdsState = mainUiState.value.activeRdsData

    LinearProgressIndicator(
        progress = { 1f },
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        trackColor = MaterialTheme.colorScheme.secondary
    )
    Box(Modifier.fillMaxWidth()) {
        IconButton(
            onClick = { context.onShareButton() },
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_share),
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            PlayerButton(serviceState) { onPlayButton(playerData) }
        }
    }
    AnimatedVisibility(activeRdsState?.next1 != null) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NextPreviousSong { activeRdsState?.next1 }
            Spacer(Modifier.padding(12.dp))
            VerticalDivider(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.width(2.dp).height(32.dp)
            )
            Spacer(Modifier.padding(12.dp))
            NextPreviousSong { activeRdsState?.next2 }
        }
    }
}


@Composable
private fun NextPreviousSong(valueProvider: () -> NextSong?) {
    val configuration = LocalConfiguration.current
    val data = valueProvider()
    Column(Modifier.width(configuration.screenWidthDp.dp / 3)) {
        if(valueProvider() != null) {
            Text(
                text = data?.title?.uppercase() ?: "",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 12.sp,
            )
            Text(
                text = data?.artist ?: "",
                maxLines = 1,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                overflow = TextOverflow.Ellipsis,
                fontSize = 15.sp,
            )
        }
    }
}