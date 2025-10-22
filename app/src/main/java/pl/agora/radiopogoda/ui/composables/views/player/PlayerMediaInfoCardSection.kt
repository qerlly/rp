package pl.agora.radiopogoda.ui.composables.views.player

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
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.data.wrappers.PlayerMediaItemModel
import pl.agora.radiopogoda.infrastructure.services.music.MediaType
import pl.agora.radiopogoda.infrastructure.services.music.MusicServiceActions
import pl.agora.radiopogoda.infrastructure.services.music.MusicServiceState
import pl.agora.radiopogoda.ui.uiData.MainUiState
import pl.agora.radiopogoda.utils.onShareButton

@Composable
fun PlayerTopCardSection(
    playerData: PlayerMediaItemModel,
    serviceState: State<MusicServiceState>,
    playerPositionState: State<Float>,
    onSeekEnded: () -> Unit,
    mainUiState: State<MainUiState>,
    onSeek: (MusicServiceActions, Float?) -> Unit,
    onPlayButton: (PlayerMediaItemModel) -> Unit,
) {
    when (playerData.mediaType) {
        MediaType.PODCAST -> PlayerPodcastsTopCardSection(
            mainUiState = mainUiState,
            playerPositionState = playerPositionState,
            serviceState = serviceState,
            playerData = playerData,
            onSeekEnded = onSeekEnded,
            onSeek = onSeek,
            onPlayButton = onPlayButton
        )
        MediaType.MAIN_CHANNEL -> PlayerLiveTopCardSection(
            serviceState = serviceState,
            playerData = playerData,
            onPlayButton = onPlayButton,
        )
        MediaType.CHANNELS -> PlayerChannelsTopCardSection(
            playerData = playerData,
            serviceState = serviceState,
            onPlayButton = onPlayButton,
            mainUiState = mainUiState,
        )
    }
}

@Composable
fun PlayerPodcastsTopCardSection(
    mainUiState: State<MainUiState>,
    playerPositionState: State<Float>,
    serviceState: State<MusicServiceState>,
    playerData: PlayerMediaItemModel,
    onSeekEnded: () -> Unit,
    onSeek: (MusicServiceActions, Float?) -> Unit,
    onPlayButton: (PlayerMediaItemModel) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(8.dp)
    ) {
        val isSeekCompleted = mainUiState.value.isSeekCompleted
        val progressState = playerPositionState.value

        var seekValue by remember { mutableFloatStateOf(progressState) }

        LaunchedEffect(progressState, isSeekCompleted) {
            if (isSeekCompleted) { seekValue = progressState }
        }

        Slider(
            value = if (isSeekCompleted) progressState else seekValue,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            onValueChange = { newValue ->
                onSeekEnded()
                seekValue = newValue
            },
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.onPrimaryContainer,
                activeTrackColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            onValueChangeFinished = {
                onSeek(MusicServiceActions.REWIND_SEEK, seekValue)
            }
        )

        ButtonsBox(
            onSeek = onSeek,
            serviceState = serviceState,
            onClick = { onPlayButton(playerData) }
        )
    }
}

@Composable
private fun ButtonsBox(
    onSeek: (MusicServiceActions, Float?) -> Unit,
    serviceState: State<MusicServiceState>,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
    ) {
        val context = LocalContext.current

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

        Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.Center),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onSeek(MusicServiceActions.REWIND_BACK, null) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_backward),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(38.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            PlayerButton(serviceState, onClick)
            Spacer(modifier = Modifier.width(6.dp))
            IconButton(onClick = { onSeek(MusicServiceActions.REWIND_FORWARD, null) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_forward),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(38.dp)
                )
            }
        }
    }
}

@Composable
fun PlayerTextTitles(mainUiState: State<MainUiState>, playerData: PlayerMediaItemModel) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val title = when (playerData.mediaType) {
            MediaType.MAIN_CHANNEL -> if (mainUiState.value.radioProgram != null) stringResource(R.string.swiped_play)
            else stringResource(R.string.actual_play)
            MediaType.CHANNELS -> playerData.subtitle
            MediaType.PODCAST -> playerData.title
        }

        val subtitle = when (playerData.mediaType) {
            MediaType.MAIN_CHANNEL -> mainUiState.value.radioProgram?.program?.name ?: playerData.title
            MediaType.CHANNELS -> playerData.title
            MediaType.PODCAST -> playerData.subtitle
        }

        val author = when (playerData.mediaType) {
            MediaType.MAIN_CHANNEL -> mainUiState.value.radioProgram?.people?.joinToString(", ") { it.name }
                ?: playerData.author
            MediaType.CHANNELS -> playerData.author
            MediaType.PODCAST -> playerData.author
        }

        Text(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = title.uppercase(),
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(4.dp))

        Text(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = subtitle,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        Text(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = author,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}