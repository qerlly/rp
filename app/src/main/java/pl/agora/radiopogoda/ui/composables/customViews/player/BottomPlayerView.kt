package pl.agora.radiopogoda.ui.composables.customViews.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.animation.circular.CircularRevealPlugin
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import kotlinx.coroutines.Job
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.data.wrappers.PlayerMediaItemModel
import pl.agora.radiopogoda.infrastructure.services.music.MusicServiceState
import pl.agora.radiopogoda.ui.theme.main
import pl.agora.radiopogoda.ui.theme.secondary
import pl.agora.radiopogoda.ui.theme.white
import pl.agora.radiopogoda.ui.uiData.BottomSheetState
import pl.agora.radiopogoda.ui.uiData.MainUiState

@Composable
fun BottomPlayerView(
    modifier: Modifier,
    drawerAction: (BottomSheetState) -> Job,
    playerDataState: State<PlayerMediaItemModel?>,
    mainUiState: State<MainUiState>,
    serviceState: State<MusicServiceState>,
    onPlayButton: (PlayerMediaItemModel) -> Unit
) {
    if (serviceState.value != MusicServiceState.NOT_INIT) {
        Card(
            modifier = modifier,
            colors = CardColors(
                containerColor = secondary,
                contentColor = white,
                disabledContentColor = secondary,
                disabledContainerColor = white
            ),
            shape = RoundedCornerShape(topStartPercent = 35, topEndPercent = 35),
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CoilImage(
                    modifier = Modifier.fillMaxHeight().width(64.dp),
                    imageModel = { mainUiState.value.radioProgram?.program?.image?.link ?: playerDataState.value?.imageUri },
                    failure = {
                        Image(
                            modifier = Modifier.matchParentSize(),
                            painter = painterResource(R.drawable.logo),
                            contentDescription = null
                        )
                    },
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.Center
                    ),
                    component = rememberImageComponent {
                        +CircularRevealPlugin(duration = 350)
                    }
                )
                Column(
                    modifier = Modifier.fillMaxWidth(0.6f).fillMaxHeight().padding(6.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = mainUiState.value.radioProgram?.program?.name ?: playerDataState.value?.title
                        ?: stringResource(R.string.app_name),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Left,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 14.sp,
                    )
                    if (!playerDataState.value?.author.isNullOrBlank()|| mainUiState.value.radioProgram != null)
                        Text(
                            text = mainUiState.value.radioProgram?.people?.joinToString(", ") { it.name } ?:
                                playerDataState.value?.author ?: "",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Light,
                            textAlign = TextAlign.Left,
                            maxLines = 1,
                            fontSize = 12.sp,
                            modifier = Modifier.basicMarquee()
                        )
                }

                Row(
                    modifier = Modifier.fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    PlayButton(
                        { playerDataState.value?.let { data -> onPlayButton(data) } },
                        serviceState,
                    )
                    IconButton(onClick = { drawerAction(BottomSheetState.PLAYER_VIEW) }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_expand),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerButton(
    btnClickProvider: () -> Unit,
    painterId: Int,
) {
    IconButton(onClick = btnClickProvider) {
        Icon(
            painter = painterResource(painterId),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun PlayButton(
    btnClickProvider: () -> Unit,
    serviceState:  State<MusicServiceState>,
) {
    when(serviceState.value) {
        MusicServiceState.NOT_INIT -> PlayerButton(btnClickProvider, R.drawable.ic_play_small)
        MusicServiceState.PAUSE, MusicServiceState.PAUSED_FROM_RECEIVERS ->
            PlayerButton(btnClickProvider, R.drawable.ic_play_small)
        MusicServiceState.PLAY -> PlayerButton(btnClickProvider, R.drawable.ic_pause_small)
        MusicServiceState.PREPARE -> CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimaryContainer)
        else -> {}
    }
}