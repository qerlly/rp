package pl.agora.radiopogoda.data.api

sealed class ApiResult<out T> {
    data class Success<out T>(val value: T) : ApiResult<T>()
    data object Failure: ApiResult<Nothing>()
    data object Loading : ApiResult<Nothing>()
}