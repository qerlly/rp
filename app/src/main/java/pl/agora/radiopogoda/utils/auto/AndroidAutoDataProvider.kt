package pl.agora.radiopogoda.utils.auto

import pl.agora.radiopogoda.data.api.ApiResult
import pl.agora.radiopogoda.data.model.channel.Channel
import pl.agora.radiopogoda.data.wrappers.PlayerMediaItemModel
import pl.agora.radiopogoda.infrastructure.repositories.ChannelsRepository
import pl.agora.radiopogoda.infrastructure.services.music.MediaType
import pl.agora.radiopogoda.utils.RDSHelper.parseRdsData
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import androidx.core.net.toUri
import pl.agora.radiopogoda.R

class AndroidAutoDataProvider @Inject constructor(
    private val channelsRepository: ChannelsRepository,
    @ApplicationContext private val context: Context,
) {

    private val _channels = MutableStateFlow<List<Channel>?>(null)
    val channels: StateFlow<List<Channel>?> = _channels

    val pickedChannel = MutableStateFlow<String?>(null)

    suspend fun getChannelsList(onUpdate: () -> Unit) {
        val channelsState = channelsRepository.getChannels()
        _channels.value = if (channelsState is ApiResult.Success) {
            channelsState.value.map { channel ->
                val words = channel.title.split(" ")
                channel.copy(title = if (words.size > 3) words.drop(3).joinToString(" ") else channel.title)
            }
        } else null
        onUpdate()
    }

    fun provideChannels(): MutableList<MediaBrowserCompat.MediaItem> {
        val mediaItems: MutableList<MediaBrowserCompat.MediaItem> = mutableListOf()
        channels.value?.forEach { channel ->
            val description = MediaDescriptionCompat.Builder()
                .setMediaId(channel.node_id.toString())
                .setTitle(channel.title)
                .build()

            val mediaItem = MediaBrowserCompat.MediaItem(
                description,
                MediaBrowserCompat.MediaItem.FLAG_BROWSABLE
            )
            mediaItems.add(mediaItem)
        }
        return mediaItems
    }

    suspend fun provideChannelRds(parentId: String): MutableList<MediaBrowserCompat.MediaItem> {
        val mediaItems: MutableList<MediaBrowserCompat.MediaItem> = mutableListOf()
        channels.value?.firstOrNull { it.node_id.toString() == parentId }?.let { channel ->
            channelsRepository.getRds(channel.rds).parseRdsData()?.let { rds ->
                val description = MediaDescriptionCompat.Builder()
                    .setMediaId(parentId)
                    .setTitle(rds.now.title)
                    .setSubtitle(rds.now.artist)
                    .setIconUri(rds.now.img.toUri())
                    .setMediaUri(channel.player.stream.toUri())
                    .build()

                val mediaItem = MediaBrowserCompat.MediaItem(
                    description,
                    MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                )
                mediaItems.add(mediaItem)
            }
        }
        return mediaItems
    }

    suspend fun getPlayerTitleInfoModel(id: String): PlayerMediaItemModel? {
        val channelData = channels.value
        if (channelData != null) {
            channelData.firstOrNull { it.node_id.toString() == id }?.let {
                pickedChannel.value = id
                val rds = channelsRepository.getRds(it.rds).parseRdsData()
                val titlesInfoModel = PlayerMediaItemModel(
                    title = rds?.now?.title ?: context.getString(R.string.app_name),
                    author = rds?.now?.artist ?: context.getString(R.string.app_name),
                    rds = it.rds,
                    uri = it.player.stream,
                    imageUri = rds?.now?.img ?: "",
                    subtitle = it.title,
                    nodeId = it.node_id.toString(),
                    mediaType = MediaType.CHANNELS,
                    songId = it.node_id.toString(),
                    duration = -1,
                )
                return titlesInfoModel
            }
        }
        return null
    }
}
