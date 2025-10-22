package pl.agora.radiopogoda.data.wrappers

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import pl.agora.radiopogoda.infrastructure.services.music.MediaType

@Parcelize
data class PlayerMediaItemModel(
    val songId: String,
    val title: String = "Radio Pogoda",
    val subtitle: String,
    val author: String,
    val mediaType: MediaType = MediaType.MAIN_CHANNEL,
    val uri: String,
    val imageUri: String,
    val rds: String?,
    val nodeId: String?,
    val duration: Int = -1,
) : Parcelable {

    companion object {
        const val MEDIA_KEY = "player_media_item"
    }
}