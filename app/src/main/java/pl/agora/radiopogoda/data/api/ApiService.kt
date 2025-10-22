package pl.agora.radiopogoda.data.api

import pl.agora.radiopogoda.data.model.Contests
import pl.agora.radiopogoda.utils.Consts
import pl.agora.radiopogoda.data.model.channel.Channel
import pl.agora.radiopogoda.data.model.news.category.CategoryNews
import okhttp3.ResponseBody
import pl.agora.radiopogoda.data.model.podcast.Podcasts
import pl.agora.radiopogoda.data.model.podcast.RadioPrograms
import pl.agora.radiopogoda.data.model.swipedInfo.RadioProgramItemWithText
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {

    @GET
    suspend fun getContests(@Url url: String): Contests

    @GET
    suspend fun getCategoryNews(@Url url: String): CategoryNews

    @GET
    suspend fun getPrograms(@Url url: String): List<RadioPrograms>

    @GET
    suspend fun getLatestPodcasts(@Url url: String): Podcasts

    @GET
    suspend fun getChannels(@Url url: String): List<Channel>

    @GET
    suspend fun getRds(@Url url: String): ResponseBody

    @GET
    suspend fun getHistoryChannel(@Url url: String): ResponseBody

    @GET(Consts.ON_RADIO_URL)
    suspend fun getOnRadioProgram(): List<RadioProgramItemWithText>
}