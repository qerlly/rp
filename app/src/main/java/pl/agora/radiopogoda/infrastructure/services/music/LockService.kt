package pl.agora.radiopogoda.infrastructure.services.music

import android.annotation.SuppressLint
import android.os.PowerManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LockService @Inject constructor(private val powerManager: PowerManager){

    private var wakeLock: PowerManager.WakeLock? = null

    @SuppressLint("WakelockTimeout")
    @Synchronized
    fun lockCPU() {
        if (wakeLock?.isHeld != true) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.javaClass.simpleName)
            wakeLock?.acquire()
        }
    }

    @Synchronized
    fun unlockCPU() {
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
            wakeLock = null
        }
    }
}