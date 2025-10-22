package pl.agora.radiopogoda.ui.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import pl.agora.radiopogoda.R
import pl.agora.radiopogoda.data.api.ApiResult
import pl.agora.radiopogoda.data.model.channel.Channel
import pl.agora.radiopogoda.data.model.channel.Channel.Companion.toMediaData
import pl.agora.radiopogoda.data.model.podcast.Podcast
import pl.agora.radiopogoda.data.model.podcast.Podcast.Companion.toMediaData
import pl.agora.radiopogoda.data.model.podcast.Podcasts
import pl.agora.radiopogoda.data.model.swipedInfo.RadioProgramItem
import pl.agora.radiopogoda.data.wrappers.PlayerMediaItemModel
import pl.agora.radiopogoda.infrastructure.AppPreferences
import pl.agora.radiopogoda.infrastructure.repositories.ChannelsRepository
import pl.agora.radiopogoda.infrastructure.repositories.ContestRepository
import pl.agora.radiopogoda.infrastructure.repositories.PodcastRepository
import pl.agora.radiopogoda.infrastructure.services.ads.AdsService
import pl.agora.radiopogoda.infrastructure.services.music.MediaType
import pl.agora.radiopogoda.infrastructure.services.music.MusicService
import pl.agora.radiopogoda.infrastructure.services.music.MusicServiceState
import pl.agora.radiopogoda.utils.Consts
import pl.agora.radiopogoda.utils.Consts.MAIN_CHANNEL
import pl.agora.radiopogoda.utils.Consts.UPDATE_DELAY
import pl.agora.radiopogoda.utils.RDSHelper.parseRdsData
import pl.agora.radiopogoda.utils.checkIfDateIsAfter
import pl.agora.radiopogoda.utils.getActualInfoFromList
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val adsService: AdsService,
    private val channelsRepository: ChannelsRepository,
    private val contestRepository: ContestRepository,
    private val podcastRepository: PodcastRepository,
    private val appPreferences: AppPreferences
) : BaseServiceControllerVM(context) {

    private var channelJob: Job? = null
    private var radioProgramJob: Job? = null
    private var activeRdsJob: Job? = null
    private var podcastProgramJob: Job? = null

    fun saveCity(value: String) = viewModelScope.launch {
        appPreferences.setCity(value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val currentProgramPodcasts = playerMediaData
        .map { it?.nodeId }
        .distinctUntilChanged()
        .filterNotNull()
        .mapLatest { nodeId ->
            _mainUiState.update { it.copy(currentProgramPodcasts = ApiResult.Loading) }
            val pickedPodcasts = mainUiState.value.podcasts
            val pickedAudioBooksPodcasts = mainUiState.value.audioPodcasts

            val podcasts = if (pickedPodcasts is ApiResult.Success) {
                val filteredPodcasts = pickedPodcasts.value.data.filter { it.program?.node_id == nodeId }
                if (filteredPodcasts.isNotEmpty()) {
                    ApiResult.Success(Podcasts(filteredPodcasts))
                } else {
                    if (pickedAudioBooksPodcasts is ApiResult.Success) {
                        val audioFilteredPodcasts = pickedAudioBooksPodcasts.value.data.filter { it.program?.node_id == nodeId }
                        ApiResult.Success(Podcasts(audioFilteredPodcasts))
                    } else {
                        ApiResult.Success(Podcasts(emptyList()))
                    }
                }
            } else {
                if (pickedAudioBooksPodcasts is ApiResult.Success) {
                    val audioFilteredPodcasts = pickedAudioBooksPodcasts.value.data.filter { it.program?.node_id == nodeId }
                    ApiResult.Success(Podcasts(audioFilteredPodcasts))
                } else {
                    ApiResult.Success(Podcasts(emptyList()))
                }
            }

            _mainUiState.update { it.copy(currentProgramPodcasts = podcasts) }
            podcasts
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ApiResult.Loading
        )

    fun onChannelCard(channel: Channel) {
        channelJob?.cancel()
        channelJob = viewModelScope.launch {
            _mainUiState.update { it.copy(channelsBar = channel.node_id.toString()) }
            val channels = _mainUiState.value.channels
            if (channels is ApiResult.Success) {
                channels.value.find { it.node_id == channel.node_id }?.let {
                    val rdsState = channelsRepository.getRds(it.rds).parseRdsData()
                    if (rdsState != null) {
                        onPlayButton(
                            channel.toMediaData(rdsState.now.title, rdsState.now.artist)
                                .copy(imageUri = rdsState.now.img)
                        )
                    }
                }
            }
            _mainUiState.update { it.copy(channelsBar = null) }
        }
    }

    fun onProgramCard(id: String) {
        podcastProgramJob?.cancel()
        podcastProgramJob = viewModelScope.launch {
            _mainUiState.value = _mainUiState.value.copy(pickedProgramPodcasts = id)
            val pickedPodcasts = mainUiState.value.podcasts

            if (pickedPodcasts is ApiResult.Success && pickedPodcasts.value.data.isNotEmpty()) {
                val data = pickedPodcasts.value.data.firstOrNull {
                    it.presenter.any { presenter -> presenter.node_id == id }
                }?.toMediaData()

                if (data != null) onPlayButton(data)
                else
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, context.getString(R.string.no_presenter), Toast.LENGTH_SHORT).show()
                    }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, context.getString(R.string.no_presenter), Toast.LENGTH_SHORT).show()
                }
            }

            _mainUiState.update { it.copy(pickedProgramPodcasts = null) }
        }
    }

    fun onAudioProgramCard(id: String) {
        podcastProgramJob?.cancel()
        podcastProgramJob = viewModelScope.launch {
            _mainUiState.value = _mainUiState.value.copy(pickedProgramPodcasts = id)
            val pickedPodcasts = mainUiState.value.audioPodcasts

            if (pickedPodcasts is ApiResult.Success && pickedPodcasts.value.data.isNotEmpty()) {
                pickedPodcasts.value.data.firstOrNull { it.program?.node_id == id }
                    ?.toMediaData()?.let {  onPlayButton(it) }
            }

            _mainUiState.update { it.copy(pickedProgramPodcasts = null) }
        }
    }

    private suspend fun getAudioBooksPodcastsWithPrograms() = supervisorScope {
        val programs = async { podcastRepository.getProgramsOfPodcasts(Consts.AUDIOBOOKS_PROGRAM_URL) }
        val podcasts = async { podcastRepository.getLatestPodcasts(Consts.AUDIOBOOKS_URL) }

        _mainUiState.update { it.copy(audioPrograms = programs.await(), audioPodcasts = podcasts.await()) }
    }

    private suspend fun getPodcastsWithPrograms() = supervisorScope {
        val programs = async { podcastRepository.getProgramsOfPodcasts(Consts.PROGRAMS_URL) }
        val podcasts = async { podcastRepository.getLatestPodcasts(Consts.PODCASTS_URL) }

        _mainUiState.update { it.copy(programs = programs.await(), podcasts = podcasts.await()) }
    }

    fun onPlayButton(mediaItem: PlayerMediaItemModel) {
        viewModelScope.launch {
            adsService.showVAST(serviceState.value) {
                when (mediaItem.mediaType) {
                    MediaType.MAIN_CHANNEL -> {
                        activeRdsJob?.cancel()
                        observeRadioProgram()
                    }
                    MediaType.CHANNELS -> {
                        _mainUiState.update { it.copy(radioProgram = null) }
                        observeActiveRdsData(mediaItem)
                    }
                    else -> {
                        _mainUiState.update { it.copy(radioProgram = null) }
                        activeRdsJob?.cancel()
                    }
                }

                when (serviceState.value) {
                    MusicServiceState.NOT_INIT -> onServiceNotInit(mediaItem)
                    MusicServiceState.PAUSE, MusicServiceState.PAUSED_FROM_RECEIVERS -> onServicePause(mediaItem)
                    else -> onServicePlayOrPrepare(mediaItem)
                }
            }
        }
    }

    private suspend fun getChannels() {
        val channels = channelsRepository.getChannels()
        _mainUiState.update { it.copy(channels = channels) }
    }

    private suspend fun updateRds() = coroutineScope {
        while (isActive) {
            val currentRds = _mainUiState.value.rdsData?.now
            val newMainRds = channelsRepository.getRds(MAIN_CHANNEL).parseRdsData()
            if (currentRds?.startDate?.checkIfDateIsAfter(newMainRds?.now?.startDate) != false) {
                _mainUiState.update { it.copy(rdsData = newMainRds) }
                playerMediaData.value?.let { mediaItem ->
                    if (mediaItem.mediaType == MediaType.MAIN_CHANNEL && newMainRds != null)
                        with(newMainRds) {
                            onTrackChanged(mediaItem.copy(title = now.title, imageUri = now.img, author = now.artist))
                        }
                }
            }
            delay(UPDATE_DELAY)
        }
    }

    private fun observeRadioProgram() {
        radioProgramJob?.cancel()
        _mainUiState.update { it.copy(radioProgram = null) }
        radioProgramJob = viewModelScope.launch {
            while (isActive) {
                delay(UPDATE_DELAY)
                if (playerMediaData.value?.mediaType == MediaType.MAIN_CHANNEL) {
                    when (val radioPrograms = mainUiState.value.radioPrograms) {
                        is ApiResult.Success<List<RadioProgramItem>> -> {
                            val radioProgram = radioPrograms.value.getActualInfoFromList()
                            _mainUiState.update { it.copy(radioProgram = radioProgram) }
                            delay(UPDATE_DELAY / 2)
                            _mainUiState.update { it.copy(radioProgram = null) }
                        }
                        else -> _mainUiState.update { it.copy(radioProgram = null) }
                    }
                } else radioProgramJob?.cancel()
            }
        }
    }

    private fun observeActiveRdsData(mediaItem: PlayerMediaItemModel) {
        activeRdsJob?.cancel()
        activeRdsJob = viewModelScope.launch {
            while (isActive) {
                if (mediaItem.mediaType == MediaType.CHANNELS && mediaItem.rds != null) {
                    val rds = channelsRepository.getRds(mediaItem.rds).parseRdsData()
                    _mainUiState.update { it.copy(activeRdsData = rds) }
                    if (mediaItem.title != rds?.now?.title && rds?.now != null) {
                        onTrackChanged(
                            mediaItem.copy(title = rds.now.title, imageUri = rds.now.img, author = rds.now.artist)
                        )
                    }
                } else activeRdsJob?.cancel()
                delay(UPDATE_DELAY)
            }
        }
    }

    private suspend fun observeForNextPodcast() {
        MusicService.state.filter { it == MusicServiceState.WAIT_FOR_NEXT_PODCAST }
            .collect {
                val currentUri = playerMediaData.value?.uri
                val programs = currentProgramPodcasts.value as? ApiResult.Success<Podcasts>
                programs?.value?.data?.let { list ->
                    val currentIndex = list.indexOfFirst { it.player.stream == currentUri }
                    list.getOrNull(currentIndex + 1)?.let {
                        onPlayButton(it.toMediaData())
                    } ?: onDestroyService()
                }
            }
    }

    private suspend fun getRadioProgramsList() {
        val radioPrograms = channelsRepository.getRadioProgramsList()
        _mainUiState.update { it.copy(radioPrograms = radioPrograms) }
    }

    private suspend fun getContests() {
        val contests = contestRepository.getActiveContests()
        _mainUiState.update { it.copy(contests = contests) }
    }

    init {
        _mainUiState.value = _mainUiState.value.copy(cities = appPreferences.city)
        viewModelScope.launch {
            supervisorScope {
                launch { getChannels() }
                launch { getRadioProgramsList() }
                launch { updateRds() }
                launch { getContests() }
                launch { getAudioBooksPodcastsWithPrograms() }
                launch { getPodcastsWithPrograms() }
                launch { observeForNextPodcast() }
            }
        }
    }

    override fun onCleared() {
        onDestroyService()
        super.onCleared()
    }
}