package pl.agora.radiopogoda.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gemius.sdk.audience.BaseEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.agora.radiopogoda.data.api.ApiResult
import pl.agora.radiopogoda.data.model.news.NewsModel
import pl.agora.radiopogoda.infrastructure.analytics.AnalyticsKey
import pl.agora.radiopogoda.infrastructure.analytics.GemiusEventAudienceManager
import pl.agora.radiopogoda.infrastructure.repositories.NewsRepository
import pl.agora.radiopogoda.ui.composables.navigation.Destinations
import pl.agora.radiopogoda.utils.Consts
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repository: NewsRepository,
    private val gemiusEventManager: GemiusEventAudienceManager,
) : ViewModel() {

    fun onTabChanged(name: String) {
        gemiusEventManager.onNewAudienceEvent(
            key = AnalyticsKey.SCREEN,
            value = "${Destinations.news}/$name",
            eventType = BaseEvent.EventType.FULL_PAGEVIEW
        )
    }

    private val newsStates = mapOf(
        Consts.ALL_NEWS_URL to MutableStateFlow<ApiResult<List<NewsModel>>>(ApiResult.Loading),
        Consts.MUSIC_NEWS_URL to MutableStateFlow<ApiResult<List<NewsModel>>>(ApiResult.Loading),
        Consts.INTERESTING_NEWS_URL to MutableStateFlow<ApiResult<List<NewsModel>>>(ApiResult.Loading),
        Consts.ENTERTAIMENT_NEWS_URL to MutableStateFlow<ApiResult<List<NewsModel>>>(ApiResult.Loading),
        Consts.QUIZ_NEWS_URL to MutableStateFlow<ApiResult<List<NewsModel>>>(ApiResult.Loading),
        Consts.RECOMMEND_NEWS_URL to MutableStateFlow<ApiResult<List<NewsModel>>>(ApiResult.Loading),
    )

    private fun fetchNews(url: String, state: MutableStateFlow<ApiResult<List<NewsModel>>>) =
        viewModelScope.launch(Dispatchers.IO) {
            if (state.value !is ApiResult.Success) {
                val response = repository.getCategoryNews(url)
                state.value = response
            }
        }

    fun getState(url: String): StateFlow<ApiResult<List<NewsModel>>> {
        val state = newsStates[url] ?: newsStates[Consts.ALL_NEWS_URL]!!
        fetchNews(url, state)
        return state
    }

    fun getAllState(): StateFlow<ApiResult<List<NewsModel>>> {
        val categoryFlows = newsStates.map { (url, stateFlow) ->
            flow {
                fetchNews(url, stateFlow)
                emitAll(stateFlow)
            }
        }

        return combine(categoryFlows) { results: Array<ApiResult<List<NewsModel>>> ->
            if (results.any { it is ApiResult.Loading }) {
                ApiResult.Loading
            }

            else if (results.any { it is ApiResult.Failure }) {
                ApiResult.Failure
            }
            else {
                val successLists = results
                    .filterIsInstance<ApiResult.Success<List<NewsModel>>>()
                    .map { it.value.take(5) }

                val mixed = mutableListOf<NewsModel>()
                var index = 0
                while (successLists.any { it.size > index }) {
                    for (list in successLists) {
                        if (index < list.size) {
                            mixed.add(list[index])
                        }
                    }
                    index++
                }

                ApiResult.Success(mixed.distinctBy { it.id })
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = ApiResult.Loading
        )
    }
}