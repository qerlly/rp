package pl.agora.radiopogoda.ui.composables.customViews.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.palette.graphics.Palette
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.palette.PalettePlugin
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.data.api.ApiResult
import pl.agora.radiopogoda.data.model.channel.Channel
import pl.agora.radiopogoda.data.model.channel.Channel.Companion.toMediaData
import pl.agora.radiopogoda.data.wrappers.PlayerMediaItemModel
import pl.agora.radiopogoda.infrastructure.services.music.MediaType
import pl.agora.radiopogoda.infrastructure.services.music.MusicServiceState
import pl.agora.radiopogoda.ui.composables.customViews.ErrorView
import pl.agora.radiopogoda.ui.theme.black
import pl.agora.radiopogoda.ui.theme.cardBlackTransparent
import pl.agora.radiopogoda.ui.theme.white
import pl.agora.radiopogoda.ui.theme.whiteTransparent
import pl.agora.radiopogoda.ui.uiData.MainUiState
import pl.agora.radiopogoda.utils.Consts

@Composable
fun PlayCardSection(
    mainUiState: State<MainUiState>,
    playerDataState: State<PlayerMediaItemModel?>,
    serviceState: State<MusicServiceState>,
    onPlayButton: (PlayerMediaItemModel) -> Unit,
) = BoxWithConstraints(Modifier.fillMaxWidth()) {

    val palette = remember { mutableStateOf<Palette?>(null) }
    val channelsState = mainUiState.value.channels
    val width = maxWidth

    Box(
        Modifier.fillMaxWidth().height((width.value / 1.2).dp).background(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(palette.value?.dominantSwatch?.rgb ?: MaterialTheme.colorScheme.primary.toArgb()),
                    MaterialTheme.colorScheme.primary
                )
            )
        )
    ) {
        when (channelsState) {

            is ApiResult.Loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primaryContainer
            )

            is ApiResult.Failure -> ErrorView(
                text = stringResource(R.string.retrofit_error),
                modifier = Modifier.width(250.dp).align(Alignment.Center),
            )

            is ApiResult.Success -> {
                val mainChannel =  remember {
                    channelsState.value.first { it.node_id == Consts.MAIN_CHANNEL_ID }
                }

                val mainRdsState = mainUiState.value.rdsData

                Card(
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    modifier = Modifier.size((width.value / 1.5).dp).align(Alignment.Center),
                ) {
                    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary)) {
                        CoilImage(
                            imageModel = { mainRdsState?.now?.img },
                            failure = {
                                Image(
                                    modifier = Modifier.matchParentSize(),
                                    painter = painterResource(R.drawable.logo),
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.fillMaxSize(),
                            imageOptions = ImageOptions(
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.Center
                            ),
                            component = rememberImageComponent {
                                +PalettePlugin { palette.value = it }
                            }
                        )
                        Box(
                            Modifier.fillMaxSize().background(
                                brush = Brush.verticalGradient(listOf(whiteTransparent, cardBlackTransparent))
                            )
                        )
                        PlayButton(
                            serviceState = serviceState,
                            onClick = {
                                onPlayButton(
                                    mainChannel.toMediaData(
                                        mediaType = MediaType.MAIN_CHANNEL,
                                        rdsTitle = mainRdsState?.now?.title,
                                        rdsAuthor = mainRdsState?.now?.artist,
                                    ).copy(imageUri = mainRdsState?.now?.img.toString())
                                )
                            },
                            playerDataState = playerDataState,
                            mainChannel = mainChannel,
                            modifier = Modifier.align(Alignment.Center).size(64.dp),
                        )
                        CardTitleZone(
                            modifier = Modifier.align(Alignment.BottomStart).padding(10.dp).fillMaxWidth(),
                            author = mainRdsState?.now?.artist,
                            title = mainRdsState?.now?.title,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayButton(
    serviceState: State<MusicServiceState>,
    onClick: () -> Unit,
    playerDataState: State<PlayerMediaItemModel?>,
    modifier: Modifier,
    mainChannel: Channel,
) {
    val serviceStateData = serviceState.value
    val isMainChannel = playerDataState.value?.uri == mainChannel.player.stream
    when {
        serviceStateData == MusicServiceState.NOT_INIT ->
            PlayerButton(modifier, R.drawable.play_main, onClick)
        serviceStateData == MusicServiceState.PAUSE || serviceStateData == MusicServiceState.PAUSED_FROM_RECEIVERS ->
            PlayerButton(modifier, R.drawable.play_main, onClick)
        serviceStateData == MusicServiceState.PLAY && isMainChannel ->
            PlayerButton(modifier, R.drawable.pause_player_main, onClick)
        serviceStateData == MusicServiceState.PREPARE -> CircularProgressIndicator(
            modifier = modifier.size(64.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        )
        else -> PlayerButton(modifier, R.drawable.play_main, onClick)
    }
}

@Composable
private fun PlayerButton(
    modifier: Modifier,
    painterId: Int,
    onClick: () -> Unit,
) = IconButton(
    modifier = modifier,
    onClick = onClick,
    content = {
        Icon(
            painter = painterResource(painterId),
            contentDescription = null,
            tint= Color.Unspecified,
            modifier = Modifier.fillMaxSize()
        )
    }
)

@Composable
private fun CardTitleZone(
    modifier: Modifier,
    author: String?,
    title: String?,
) {
    Column(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Card(shape = RoundedCornerShape(4.dp)) {
                Box(modifier = Modifier.background(white)) {
                    Text(
                        text = stringResource(R.string.on_air),
                        color = black,
                        modifier = Modifier.background(white).padding(2.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
            Text(
                text = stringResource(R.string.actual_play),
                color = white,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.fillMaxWidth(0.8f)) {
                title?.let {
                    Text(
                        text = title,
                        color = white,
                        fontSize = 12.sp,
                        lineHeight = 15.sp,
                        modifier = Modifier.padding(top = 4.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(6.dp))
                author?.let {
                    Text(
                        text = author,
                        color = white,
                        fontSize = 11.sp,
                        lineHeight = 13.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}