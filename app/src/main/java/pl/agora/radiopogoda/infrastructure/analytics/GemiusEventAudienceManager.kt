package pl.agora.radiopogoda.infrastructure.analytics

import android.content.Context
import com.gemius.sdk.audience.AudienceEvent

import com.gemius.sdk.audience.BaseEvent.EventType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GemiusEventAudienceManager @Inject constructor(
    @ApplicationContext private val context: Context,
): IAnalyticsAudienceManager<EventType> {

    override fun onNewAudienceEvent(key: AnalyticsKey, value: String, eventType: EventType) {
        val event = AudienceEvent(context)
        event.eventType = eventType
        event.addExtraParameter(key.name, value)
        event.sendEvent()
    }
}