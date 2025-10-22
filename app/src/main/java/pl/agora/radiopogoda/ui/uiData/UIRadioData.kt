package pl.agora.radiopogoda.ui.uiData

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pl.agora.radiopogoda.data.api.ApiResult
import pl.agora.radiopogoda.data.model.Contests
import pl.agora.radiopogoda.data.model.channel.Channel
import pl.agora.radiopogoda.data.model.podcast.Podcasts
import pl.agora.radiopogoda.data.model.podcast.RadioPrograms
import pl.agora.radiopogoda.data.model.rds.RdsData
import pl.agora.radiopogoda.data.model.swipedInfo.RadioProgramItem

@Stable
data class MainUiState(
    val channels: ApiResult<List<Channel>> = ApiResult.Loading,
    val channelsBar: String? = null,
    val programs: ApiResult<List<RadioPrograms>> = ApiResult.Loading,
    val audioPrograms: ApiResult<List<RadioPrograms>> = ApiResult.Loading,
    val podcasts: ApiResult<Podcasts> = ApiResult.Loading,
    val audioPodcasts: ApiResult<Podcasts> = ApiResult.Loading,
    val radioProgram: RadioProgramItem? = null,
    val radioPrograms: ApiResult<List<RadioProgramItem>> = ApiResult.Loading,
    val rdsData: RdsData? = null,
    val activeRdsData: RdsData? = null,
    val pickedProgramPodcasts: String? = null,
    val currentProgramPodcasts: ApiResult<Podcasts> = ApiResult.Loading,
    val isSeekCompleted: Boolean = true,
    val contests: ApiResult<Contests> = ApiResult.Loading,
    val cities: Flow<String?> = flow { emit(null) }
)