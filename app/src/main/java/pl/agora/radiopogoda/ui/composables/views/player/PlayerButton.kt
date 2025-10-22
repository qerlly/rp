package pl.agora.radiopogoda.ui.composables.views.player

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.infrastructure.services.music.MusicServiceState
import pl.agora.radiopogoda.ui.theme.white

@Composable
fun PlayerButton(
    serviceState: State<MusicServiceState>,
    onClick: () -> Unit,
) {
    val state = serviceState.value
    when(state) {
        MusicServiceState.NOT_INIT ->
            ButtonView(painterResource(R.drawable.ic_play_small), onClick)
        MusicServiceState.PAUSE, MusicServiceState.PAUSED_FROM_RECEIVERS ->
            ButtonView(painterResource(R.drawable.ic_play_small), onClick)
        MusicServiceState.PLAY ->
            ButtonView(painterResource(R.drawable.ic_pause_small), onClick)
        MusicServiceState.PREPARE, MusicServiceState.WAIT_FOR_NEXT_PODCAST ->
            CircularProgressIndicator(
                modifier = Modifier.size(68.dp),
                color = white,
                trackColor = MaterialTheme.colorScheme.primaryContainer,
            )
    }
}

@Composable
private fun ButtonView(painter: Painter, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(64.dp)) {
        Icon(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}