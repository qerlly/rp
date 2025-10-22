package pl.agora.radiopogoda.data.model

import com.squareup.moshi.Json

data class Contests(
    @Json(name="item")
    val contests: List<Contest>
)

data class Contest(
    val title: String,
    val url: String,
    val link: String,
    val parent: String,
)