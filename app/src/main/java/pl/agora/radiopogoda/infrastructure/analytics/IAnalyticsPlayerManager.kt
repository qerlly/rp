package pl.agora.radiopogoda.infrastructure.analytics

import pl.agora.radiopogoda.data.wrappers.PlayerMediaItemModel

interface IAnalyticsPlayerManager<T> {

    fun onNewProgram(mediaItem: PlayerMediaItemModel)

    fun onNewPlayerEvent(eventType: T)
}