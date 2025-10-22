package pl.agora.radiopogoda.data.model.podcast

import com.squareup.moshi.Json

data class Image(
    @Json(name="size-800_jpg")
    val link: String,
)