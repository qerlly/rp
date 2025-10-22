package pl.agora.radiopogoda.data.model.history

import androidx.compose.runtime.Stable
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Stable
data class History(
    val img: String,
    val rds_artist: String,
    val rds_duration: String,
    val rds_end: String,
    val rds_song_id: String,
    val rds_start: String,
    val rds_title: String
) {
    fun getHour(): Int {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val date = LocalDateTime.parse(rds_start, formatter)
        val convertedDate = date.atZone(ZoneId.of("Europe/Warsaw"))
            .withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
        return convertedDate.hour
    }

    fun getTime(): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val date = LocalDateTime.parse(rds_start, formatter)
        val convertedDate = date.atZone(ZoneId.of("Europe/Warsaw"))
            .withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
        val hour =
            if (convertedDate.hour < 10) "0${convertedDate.hour}" else convertedDate.hour.toString()
        val minutes =
            if (convertedDate.minute < 10) "0${convertedDate.minute}" else convertedDate.minute.toString()
        return "$hour:$minutes"
    }
}