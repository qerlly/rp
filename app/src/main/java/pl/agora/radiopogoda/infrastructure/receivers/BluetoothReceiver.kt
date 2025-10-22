package pl.agora.radiopogoda.infrastructure.receivers

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class BluetoothReceiver @Inject constructor(
    @ApplicationContext context: Context,
) : BaseReceiver<BluetoothState>(context, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)) {

    override val stateFlow: MutableStateFlow<BluetoothState> = MutableStateFlow(BluetoothState.IDLE)

    override fun createReceiver(): BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                stateFlow.value = when (state) {
                    BluetoothAdapter.STATE_OFF, BluetoothAdapter.STATE_TURNING_OFF -> BluetoothState.TURN_OFF
                    BluetoothAdapter.STATE_ON, BluetoothAdapter.STATE_TURNING_ON -> BluetoothState.TURN_ON
                    else -> stateFlow.value
                }
            }
        }
    }
}

enum class BluetoothState { IDLE, TURN_ON, TURN_OFF }