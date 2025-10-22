package pl.agora.radiopogoda.utils

import java.time.LocalTime
import java.time.format.DateTimeFormatter

object TimeHelper {

    var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun formatRemainingTime(endTime: Long): String {
        val remainingMillis = endTime - System.currentTimeMillis()
        val totalMinutes = (remainingMillis / 60000).coerceAtLeast(0L)
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        return when {
            hours > 0 && minutes > 0 ->
                " - pozostało $hours ${getHoursDeclension(hours)} i $minutes ${getMinutesDeclension(minutes)}"
            hours > 0 ->
                " - pozostało $hours ${getHoursDeclension(hours)}"
            else ->
                " - pozostało $minutes ${getMinutesDeclension(minutes)}"
        }
    }

    private fun getMinutesDeclension(minutes: Long): String {
        return when {
            minutes % 10 == 1L && minutes % 100 != 11L -> "minuta"
            minutes % 10 in 2..4 && minutes % 100 !in 12L..14L -> "minuty"
            else -> "minut"
        }
    }

    private fun getHoursDeclension(hours: Long): String {
        return when {
            hours % 10 == 1L && hours % 100 != 11L -> "godzina"
            hours % 10 in 2..4 && hours % 100 !in 12L..14L -> "godziny"
            else -> "godzin"
        }
    }

    fun formatSecondsToTime(seconds: Int): String {
        return if (seconds >= 86400) {
            "00:00"
        } else {
            val time = LocalTime.ofSecondOfDay(seconds.toLong())
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            time.format(formatter)
        }
    }
}