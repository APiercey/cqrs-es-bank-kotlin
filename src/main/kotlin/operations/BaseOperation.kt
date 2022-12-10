package operations

import com.eventstore.dbclient.EventData
import events.BaseEvent

open class BaseOperation {
    fun buildEventData(event: BaseEvent) : EventData {
        return EventData
            .builderAsJson<Any>(event.eventType(), event)
            .build()
    }
}