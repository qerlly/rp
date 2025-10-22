package pl.agora.radiopogoda.infrastructure.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.TelephonyManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class CallReceiver @Inject constructor(
    @ApplicationContext context: Context,
) : BaseReceiver<CallState>(context, IntentFilter(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {

    override val stateFlow: MutableStateFlow<CallState> = MutableStateFlow(CallState.IDLE)

    override fun createReceiver(): BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
                when (intent.getStringExtra(TelephonyManager.EXTRA_STATE)) {
                    TelephonyManager.EXTRA_STATE_RINGING -> stateFlow.value = CallState.STARTED
                    TelephonyManager.EXTRA_STATE_IDLE -> stateFlow.value = CallState.ENDED
                }
            }
        }
    }
}

enum class CallState { IDLE, STARTED, ENDED }