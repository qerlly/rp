package pl.agora.radiopogoda.ui.composables.views.pocast

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.data.model.podcast.Podcast
import pl.agora.radiopogoda.data.model.podcast.Podcast.Companion.toMediaData
import pl.agora.radiopogoda.data.wrappers.PlayerMediaItemModel
import pl.agora.radiopogoda.infrastructure.services.music.MusicServiceState
import pl.agora.radiopogoda.ui.theme.black
import pl.agora.radiopogoda.utils.toDateString
import pl.agora.radiopogoda.utils.toMinutesString

@Composable
fun PodcastCard(
    podcast: Podcast,
    playerMediaDataState: State<PlayerMediaItemModel?>,
    serviceState: State<MusicServiceState>,
    onPodcast: (PlayerMediaItemModel) -> Unit,
    showProgramName: Boolean = false,
) = Box(Modifier.fillMaxWidth().padding(16.dp)){
    Column(Modifier.fillMaxSize()) {
        TopCardSection(podcast, showProgramName)
        Spacer(Modifier.height(4.dp))
        BottomCardSection(podcast)
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp).height(1.dp),
            color = MaterialTheme.colorScheme.secondary
        )
    }
    ActionButtons(
        podcast = podcast,
        onPodcast = onPodcast,
        playerMediaDataState = playerMediaDataState,
        serviceState = serviceState,
        modifier = Modifier.align(Alignment.BottomEnd).padding(bottom = 12.dp, end = 2.dp)
    )
}

@Composable
private fun ActionButtons(
    podcast: Podcast,
    onPodcast: (PlayerMediaItemModel) -> Unit,
    playerMediaDataState: State<PlayerMediaItemModel?>,
    serviceState: State<MusicServiceState>,
    modifier: Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End,
    ) {
        ColoredPlayPauseButton(onPodcast, podcast, playerMediaDataState, serviceState)
    }
}

@Composable
private fun TopCardSection(podcast: Podcast, showProgramName: Boolean) = Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.Top,
    horizontalArrangement = Arrangement.SpaceBetween
) {
    Column(Modifier.fillMaxWidth(0.7f).padding(end = 8.dp)) {
        if (showProgramName && podcast.program?.title != null) {
            Text(
                text = podcast.program.title.uppercase(),
                textAlign = TextAlign.Justify,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium,
                color = black,
            )
        }
        Text(
            text = podcast.title,
            textAlign = TextAlign.Justify,
            fontSize = 14.sp,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
    Image(
        painter = rememberAsyncImagePainter(
            model = podcast.program?.image?.link,
            error = painterResource(R.drawable.logo)
        ),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(86.dp).clip(RoundedCornerShape(10.dp)),
    )
}

@Composable
private fun BottomCardSection(podcast: Podcast) = Row(
    modifier = Modifier.fillMaxSize(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
) {
    Column(Modifier.fillMaxWidth(0.7f)) {
        Text(
            text = podcast.presenter.map { it.title }.joinToString(", "),
            fontSize = 13.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = podcast.published_date.toDateString(),
                fontSize = 11.5.sp,
                color = black
            )
            Box(Modifier.size(3.dp).clip(CircleShape).background(MaterialTheme.colorScheme.tertiary))
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_timer_24),
                    contentDescription = null,
                    tint = black
                )
                Text(
                    text = podcast.player.duration.toMinutesString(),
                    fontSize = 11.5.sp,
                    color = black
                )
            }
        }
    }
}

@Composable
fun ColoredPlayPauseButton(
    onPodcast: (PlayerMediaItemModel) -> Unit,
    podcast: Podcast,
    playerData: State<PlayerMediaItemModel?>,
    serviceState: State<MusicServiceState>,
) {
    IconButton(onClick = { onPodcast(podcast.toMediaData()) }) {
        val isPlaying = playerData.value?.uri == podcast.player.stream &&
                serviceState.value in listOf(MusicServiceState.PLAY, MusicServiceState.PREPARE)

        Icon(
            painter = painterResource(if (isPlaying) R.drawable.ic_pause_small else R.drawable.ic_play_small),
            contentDescription = null,
            tint = black,
            modifier = Modifier.size(46.dp)
        )
    }
}