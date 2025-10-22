package pl.agora.radiopogoda.infrastructure.repositories

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.agora.radiopogoda.data.api.ApiResult

abstract class BaseRepository {
    suspend fun  <T> safeApiCall(
        apiCall: suspend () -> T
    ): ApiResult<T> {
        return withContext(Dispatchers.IO){
            try {
                ApiResult.Success(apiCall.invoke())
            } catch (throwable: Throwable){
                throwable.message?.let { Log.d("Retrofit_error", it) }
                ApiResult.Failure
            }
        }
    }
}