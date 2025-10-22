package pl.agora.radiopogoda.infrastructure.repositories

import pl.agora.radiopogoda.data.api.ApiService
import pl.agora.radiopogoda.data.model.news.NewsModel
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val apiService: ApiService
) : BaseRepository() {

    suspend fun getCategoryNews(uri: String) = safeApiCall {
        apiService.getCategoryNews(uri).item.map { NewsModel.fromCategoryToNewsModel(it) }
    }
}