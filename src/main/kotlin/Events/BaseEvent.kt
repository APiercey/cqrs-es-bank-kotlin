package Events

import com.eventstore.dbclient.EventData

open class BaseEvent {
    open fun eventType(): String { return "" }

    fun toEventData() : EventData {
        return EventData
            .builderAsJson<Any>(eventType(), this)
            .build()
    }
}