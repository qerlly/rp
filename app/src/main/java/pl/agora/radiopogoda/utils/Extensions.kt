package pl.agora.radiopogoda.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavHostController
import pl.agora.radiopogoda.data.model.swipedInfo.RadioProgramItem
import pl.agora.radiopogoda.utils.Consts.APP_LINK
import pl.agora.radiopogoda.R
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter

fun Intent.openUrl(context: Context) = try {
    context.startActivity(this)
} catch (e: Exception) {
    e.printStackTrace()
}

fun NavHostController.openDestination(destination: String) =
    this.navigate(destination) {
        this@openDestination.graph.startDestinationRoute?.let { route ->
            popUpTo(route) { saveState = true }
        }
    }

fun List<RadioProgramItem>.getActualInfoFromList(): RadioProgramItem? {
    val now = LocalTime.now()
    return this.firstOrNull {
        val start = LocalTime.MIN.plusSeconds(it.start.toLong())
        val end = LocalTime.MIN.plusSeconds(it.end.toLong())
        now in start..end || (start.isAfter(end) && now.isAfter(start))
    }
}

@SuppressLint("DefaultLocale")
fun Int.toMinutesString(): String {
    val minutes = (this % 3600) / 60
    val seconds = this % 60
    return String.format("%02d:%02d", minutes, seconds)
}

@SuppressLint("SimpleDateFormat")
fun Int.toDateString(): String {
    val format = SimpleDateFormat("dd.MM.yyyy")
    return format.format(this * 1000L)
}

fun String.checkIfDateIsAfter(date: String?): Boolean {
    if (date.isNullOrEmpty() || this.isEmpty()) return true
    return Timestamp.valueOf(date) > Timestamp.valueOf(this)
}

fun Intent.getPendingIntent(context: Context): PendingIntent {
    val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
        PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    else PendingIntent.FLAG_UPDATE_CURRENT
    return PendingIntent.getService(
        context,
        0,
        this,
        flags
    )
}

fun Context.onShareButton() {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_SUBJECT, this.getString(R.string.app_name))
    intent.putExtra(Intent.EXTRA_TEXT, APP_LINK)
    startActivity(this, Intent.createChooser(intent, "ShareWith"), null)
}

fun Long.toTimeAfterTimestamp(): String {
    val now = System.currentTimeMillis()
    val diff = now - this

    val minute = 60 * 1000L
    val hour = 60 * minute
    val day = 24 * hour

    return when {
        diff < minute -> "minutę temu"
        diff < 60 * minute -> {
            val minutes = diff / minute
            when {
                minutes == 1L -> "minutę temu"
                minutes in 2..4 -> "$minutes minuty temu"
                else -> "$minutes minut temu"
            }
        }
        diff < day -> {
            val hours = diff / hour
            when {
                hours == 1L -> "godzinę temu"
                hours in 2..4 -> "$hours godziny temu"
                else -> "$hours godzin temu"
            }
        }
        diff < 2 * day -> "dzień temu"
        else -> {
            val days = diff / day
            when {
                days == 1L -> "dzień temu"
                else -> "$days dni temu"
            }
        }
    }
}

var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
fun LocalTime.toReadableString(): String = this.format(formatter)

fun Context.isAppInForeground(): Boolean {
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val appProcesses = activityManager.runningAppProcesses ?: return false
    val myProcess = appProcesses.firstOrNull { it.pid == android.os.Process.myPid() }
    return myProcess?.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
}