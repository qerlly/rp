package pl.agora.radiopogoda.infrastructure.receivers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import pl.agora.radiopogoda.infrastructure.services.music.MusicServiceState
import javax.inject.Inject

class ReceiversManager @Inject constructor(
    private val callReceiver: CallReceiver,
    private val headsetReceiver: HeadsetReceiver,
    private val bluetoothHeadsetReceiver: BluetoothHeadsetReceiver,
    private val bluetoothReceiver: BluetoothReceiver,
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun observeState(
        play: () -> Unit,
        pause: (MusicServiceState) -> Unit,
        getServiceState: () -> MusicServiceState
    ) {
        scope.launch {
            supervisorScope {
                launch {
                    callReceiver.stateFlow.collect { callState ->
                        when (callState) {
                            CallState.STARTED -> pause(MusicServiceState.PAUSED_FROM_RECEIVERS)
                            CallState.ENDED -> if (getServiceState() == MusicServiceState.PAUSED_FROM_RECEIVERS) play()
                            else -> Unit
                        }
                    }
                }
                launch {
                    headsetReceiver.stateFlow.collect { headsetState ->
                        when (headsetState) {
                            HeadsetState.TURN_OFF -> if (getServiceState() in listOf(
                                    MusicServiceState.PLAY, MusicServiceState.PREPARE)) {
                                pause(MusicServiceState.PAUSED_FROM_RECEIVERS)
                            }
                            HeadsetState.TURN_ON -> if (getServiceState() == MusicServiceState.PAUSED_FROM_RECEIVERS) play()
                            else -> Unit
                        }
                    }
                }
                launch {
                    combine(
                        bluetoothHeadsetReceiver.stateFlow,
                        bluetoothReceiver.stateFlow,
                        ::Pair
                    ).collect { (btHeadState, btState) ->
                        when {
                            btHeadState == BluetoothHeadsetState.TURN_ON && btState == BluetoothState.TURN_OFF ->
                                pause(MusicServiceState.PAUSED_FROM_RECEIVERS)
                            btHeadState == BluetoothHeadsetState.TURN_ON && btState == BluetoothState.TURN_ON ->
                                if (getServiceState() == MusicServiceState.PAUSED_FROM_RECEIVERS) play()
                            btHeadState == BluetoothHeadsetState.TURN_OFF ->
                                pause(MusicServiceState.PAUSED_FROM_RECEIVERS)
                        }
                    }
                }
            }
        }
    }

    fun unregisterReceivers() {
        scope.launch {
            withContext(NonCancellable) {
                try {
                    callReceiver.unregisterReceiver()
                    headsetReceiver.unregisterReceiver()
                    bluetoothHeadsetReceiver.unregisterReceiver()
                    bluetoothReceiver.unregisterReceiver()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}