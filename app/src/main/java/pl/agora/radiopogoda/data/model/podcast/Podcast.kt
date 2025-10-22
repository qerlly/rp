package pl.agora.radiopogoda.data.model.podcast

import pl.agora.radiopogoda.data.wrappers.PlayerMediaItemModel
import pl.agora.radiopogoda.infrastructure.services.music.MediaType

data class Podcast(
    val node_id: Int,
    val player: Player,
    val presenter: List<Presenter>,
    val program: Program?,
    val published_date: Int,
    val title: String,
) {
    override fun equals(other: Any?): Boolean {
        return if (other is Podcast)
            this.node_id == other.node_id
        else false
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    companion object {

        fun Podcast.toMediaData(): PlayerMediaItemModel = PlayerMediaItemModel(
            songId = node_id.toString(),
            mediaType = MediaType.PODCAST,
            uri = player.stream ?: "",
            rds = null,
            title = program?.title ?: title,
            author = presenter.map { podcast -> podcast.title }.joinToString(", "),
            subtitle = title,
            imageUri = program?.image?.link ?: "",
            nodeId = program?.node_id,
            duration = player.duration
        )
    }
}