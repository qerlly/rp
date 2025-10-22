package pl.agora.radiopogoda.infrastructure.repositories

import pl.agora.radiopogoda.data.api.ApiService
import pl.agora.radiopogoda.data.model.podcast.Podcast
import pl.agora.radiopogoda.data.model.podcast.Podcasts
import pl.agora.radiopogoda.utils.Consts.PODCASTS_URL
import pl.agora.radiopogoda.utils.Consts.PROGRAMS_URL
import javax.inject.Inject

class PodcastRepository @Inject constructor(
    private val apiService: ApiService
) : BaseRepository() {


    suspend fun getProgramsOfPodcasts(url: String) = safeApiCall {
        apiService.getPrograms(url)
    }

    suspend fun getLatestPodcasts(url: String) = safeApiCall {
        val podcasts = apiService.getLatestPodcasts(url).data
        Podcasts(podcasts).filterForLatestByCategory()
    }

    private fun Podcasts.filterForLatestByCategory(): Podcasts {
        val groupedPodcasts = this.data
            .groupBy { it.program?.node_id }
            .mapValues { (_, podcasts) -> podcasts.toMutableList() }

        val interleavedData = mutableListOf<Podcast>()

        while (groupedPodcasts.values.any { it.isNotEmpty() }) {
            groupedPodcasts.values.forEach { podcasts ->
                if (podcasts.isNotEmpty()) {
                    interleavedData.add(podcasts.removeAt(0))
                }
            }
        }

        return Podcasts(interleavedData)
    }
}