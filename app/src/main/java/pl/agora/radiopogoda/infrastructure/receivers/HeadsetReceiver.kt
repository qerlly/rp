package pl.agora.radiopogoda.infrastructure.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class HeadsetReceiver @Inject constructor(
    @ApplicationContext context: Context,
): BaseReceiver<HeadsetState>(context, IntentFilter(AudioManager.ACTION_HEADSET_PLUG)) {

    private var hasReceivedInitialEvent = false

    override val stateFlow = MutableStateFlow(HeadsetState.IDLE)

    override fun createReceiver(): BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val stateValue = intent.getIntExtra("state", -1)
            when (stateValue) {
                1 -> {
                    if (hasReceivedInitialEvent) {
                        stateFlow.value = HeadsetState.TURN_ON
                    } else {
                        hasReceivedInitialEvent = true
                    }
                }

                else -> {
                    if (hasReceivedInitialEvent) {
                        stateFlow.value = HeadsetState.TURN_OFF
                    } else {
                        hasReceivedInitialEvent = true
                    }
                }
            }
        }
    }
}

enum class HeadsetState { IDLE, TURN_ON, TURN_OFF }