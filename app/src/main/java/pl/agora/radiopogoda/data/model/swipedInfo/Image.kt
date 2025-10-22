package pl.agora.radiopogoda.data.model.swipedInfo

import com.squareup.moshi.Json

data class Image(
    @Json(name = "size-800")
    val link: String,
)