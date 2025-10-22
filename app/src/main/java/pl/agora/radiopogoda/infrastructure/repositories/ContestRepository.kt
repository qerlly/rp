package pl.agora.radiopogoda.infrastructure.repositories

import pl.agora.radiopogoda.data.api.ApiService
import pl.agora.radiopogoda.utils.Consts.CONTEST_URL
import javax.inject.Inject

class ContestRepository @Inject constructor(
    private val zpService: ApiService
) : BaseRepository() {

    suspend fun getActiveContests() = safeApiCall { zpService.getContests(CONTEST_URL) }
}