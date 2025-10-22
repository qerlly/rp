package pl.agora.radiopogoda.data.model.channel

import pl.agora.radiopogoda.data.model.shared.Player
import pl.agora.radiopogoda.data.wrappers.PlayerMediaItemModel
import pl.agora.radiopogoda.infrastructure.services.music.MediaType

data class Channel(
    val desc: String,
    val node_id: Int,
    val player: Player,
    val short_desc: String,
    val title: String,
    val rds: String,
    val image: String,
) {
    companion object {
        fun Channel.toMediaData(
            rdsTitle: String?,
            rdsAuthor: String?,
            mediaType: MediaType = MediaType.CHANNELS
        ): PlayerMediaItemModel = PlayerMediaItemModel(
            songId = node_id.toString(),
            title = rdsTitle ?: title,
            subtitle = title,
            author = rdsAuthor ?: title,
            mediaType = mediaType,
            imageUri = image,
            rds = rds,
            uri = player.stream,
            nodeId = null
        )
    }
}