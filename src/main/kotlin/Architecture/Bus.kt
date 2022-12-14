package Architecture


import Architecture.Command
import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventData

class Bus(val client: EventStoreDBClient) {
    fun send(cmd : Command) {
        client.appendToStream("commands", toEventData(cmd)).get()
    }

    private fun toEventData(cmd : Command): EventData {
        return EventData
            .builderAsJson<Any>(cmd.name(), cmd)
            .build()
    }
}