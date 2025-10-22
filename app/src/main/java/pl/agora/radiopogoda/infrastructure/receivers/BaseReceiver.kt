package pl.agora.radiopogoda.infrastructure.receivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow

abstract class BaseReceiver<T>(
    @ApplicationContext protected val context: Context,
    private val filter: IntentFilter
) {
    abstract val stateFlow: MutableStateFlow<T>

    protected abstract fun createReceiver(): BroadcastReceiver

    private val receiver by lazy { createReceiver() }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun registerReceiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(receiver, filter)
        }
    }

    fun unregisterReceiver() {
        try {
            context.unregisterReceiver(receiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }
}