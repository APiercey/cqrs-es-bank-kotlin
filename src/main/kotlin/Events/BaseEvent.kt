package Events

import com.eventstore.dbclient.EventData

abstract class BaseEvent {
    fun toEventData() : EventData {
        return EventData
            .builderAsJson<Any>(eventType(), this)
            .build()
    }

    abstract fun eventType() : String
}