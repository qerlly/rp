package pl.agora.radiopogoda.infrastructure.receivers

import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class BluetoothHeadsetReceiver @Inject constructor(
    @ApplicationContext context: Context,
): BaseReceiver<BluetoothHeadsetState>(context, IntentFilter(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)) {

    private var hasReceivedInitialEvent = false

    override val stateFlow = MutableStateFlow(BluetoothHeadsetState.IDLE)

    override fun createReceiver(): BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1)) {
                BluetoothProfile.STATE_DISCONNECTED -> {
                    if (hasReceivedInitialEvent) {
                        stateFlow.value = BluetoothHeadsetState.TURN_OFF
                    } else {
                        hasReceivedInitialEvent = true
                    }
                }
                BluetoothProfile.STATE_CONNECTED -> {
                    stateFlow.value = BluetoothHeadsetState.TURN_ON
                    if (!hasReceivedInitialEvent) {
                        hasReceivedInitialEvent = true
                    }
                }
            }
        }
    }
}

enum class BluetoothHeadsetState { IDLE, TURN_ON, TURN_OFF }