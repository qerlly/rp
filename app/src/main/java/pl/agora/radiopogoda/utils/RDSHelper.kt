package pl.agora.radiopogoda.utils

import pl.agora.radiopogoda.data.api.ApiResult
import pl.agora.radiopogoda.data.model.rds.NextSong
import pl.agora.radiopogoda.data.model.rds.Now
import pl.agora.radiopogoda.data.model.rds.RdsData
import okhttp3.ResponseBody
import org.json.JSONObject

object RDSHelper {

    fun ApiResult<ResponseBody?>.parseRdsData() = (this as? ApiResult.Success)?.value?.toNowRdsData()

    private fun ResponseBody.toNowRdsData(): RdsData? {
        return try {
            val jsonString = string().drop(8).dropLast(1).takeIf { it.isNotEmpty() } ?: return defaultRdsData()

            with(JSONObject(jsonString)) {
                RdsData(
                    now = getJSONObject("now").toNow(),
                    next1 = optJSONObject("next1")?.toNextSong(),
                    next2 = optJSONObject("next2")?.toNextSong()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun JSONObject.toNow() = Now(
        artist = getString("artist"),
        title = getString("title"),
        duration = getString("duration"),
        id = getString("id"),
        img = getString("img"),
        startDate = getString("startDate")
    )

    private fun JSONObject.toNextSong() = NextSong(
        artist = getString("artist"),
        title = getString("title"),
        duration = getString("duration"),
        id = getString("id"),
        startDate = getString("startDate"),
        endDate = getString("endDate")
    )

    private fun defaultRdsData() = RdsData(
        now = Now(title = "Radio Pogoda"),
        next1 = NextSong(),
        next2 = NextSong()
    )
}