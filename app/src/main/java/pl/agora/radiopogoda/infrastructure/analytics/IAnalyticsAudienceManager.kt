package pl.agora.radiopogoda.infrastructure.analytics

interface IAnalyticsAudienceManager<T> {
    fun onNewAudienceEvent(key: AnalyticsKey, value: String, eventType: T)
}