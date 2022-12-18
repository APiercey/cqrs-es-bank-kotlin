package Architecture

import Architecture.Command
import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventData

class Bus(val client: EventStoreDBClient) {
    fun send(cmd : Command) {
        client.appendToStream("commands", toEventData(cmd)).get()
    }

    fun sendError(capturedDomainError: CapturedDomainError) {
        client.appendToStream("errors", toErrorData(capturedDomainError)).get()
    }

    private fun toEventData(cmd : Command): EventData {
        return EventData
            .builderAsJson<Any>(cmd.name(), cmd)
            .build()
    }

    private fun toErrorData(err : CapturedDomainError): EventData {
        return EventData
            .builderAsJson<Any>(err.eventType(), err)
            .build()
    }
}