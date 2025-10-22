package pl.agora.radiopogoda.infrastructure.player

import android.content.Context
import androidx.media3.common.Player
import pl.agora.radiopogoda.data.wrappers.PlayerMediaItemModel

interface IPlayer {

    fun initPlayer(context: Context, errorListener: Player.Listener)

    fun destroyPlayer()

    fun play(context: Context, errorListener: Player.Listener)

    fun play()

    fun rewindForward()

    fun rewindBack()

    fun rewindSeek(rewindValue: Float)

    fun seekPodcastToActualPosition()

    fun setMediaData(data: PlayerMediaItemModel?)

    fun resetPlayer()
}