package pl.agora.radiopogoda.infrastructure.repositories

import pl.agora.radiopogoda.data.api.ApiResult
import pl.agora.radiopogoda.data.api.ApiService
import pl.agora.radiopogoda.data.model.history.History
import pl.agora.radiopogoda.data.model.swipedInfo.RadioProgramItem
import pl.agora.radiopogoda.utils.Consts
import pl.agora.radiopogoda.utils.Consts.CHANNELS_URL
import javax.inject.Inject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChannelsRepository @Inject constructor(
    private val apiService: ApiService
) : BaseRepository() {

    suspend fun getChannels() = safeApiCall { apiService.getChannels(CHANNELS_URL) }

    suspend fun getRds(url: String) = safeApiCall { apiService.getRds(url) }

    suspend fun getHistoryChannel(date: String, hour: Int): List<History> {
        val request = Consts.HISTORY.replace("date", date)

        val result = safeApiCall {
            val rawJsonp = apiService.getHistoryChannel(request)
                .byteStream()
                .bufferedReader()
                .use { it.readText() }

            rawJsonp.toHistoryDataChunked()
        }

        return when (result) {
            is ApiResult.Success<List<History>> ->
                result.value.filter { it.getHour() == hour && it.rds_artist.isNotEmpty() }
            else -> emptyList()
        }
    }

    suspend fun getRadioProgramsList() = safeApiCall {
        apiService.getOnRadioProgram().map {
            RadioProgramItem(
                program = it.program,
                people = it.people,
                start = timeToSeconds(it.start),
                end = timeToSeconds(it.end),
            )
        }
    }

    private fun timeToSeconds(dateTimeStr: String): Int {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val dateTime = LocalDateTime.parse(dateTimeStr, formatter)
        return dateTime.toLocalTime().toSecondOfDay()
    }

    private fun String.toHistoryDataChunked(): List<History> {
        val list = mutableListOf<History>()
        val rawJson = this.substringAfter("jsonData(").substringBeforeLast(")")

        val regex = "\\{.*?\\}".toRegex(RegexOption.DOT_MATCHES_ALL)
        regex.findAll(rawJson).forEach { matchResult ->
            try {
                val obj = org.json.JSONObject(matchResult.value)
                list.add(
                    History(
                        img = obj.optString("img"),
                        rds_artist = obj.optString("rds_artist"),
                        rds_duration = obj.optString("rds_duration"),
                        rds_end = obj.optString("rds_end"),
                        rds_start = obj.optString("rds_start"),
                        rds_song_id = obj.optString("rds_song_id"),
                        rds_title = obj.optString("rds_title"),
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return list
    }
}
