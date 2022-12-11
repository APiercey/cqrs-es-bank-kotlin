package AccountsDomain.Operations

import com.eventstore.dbclient.EventStoreDBClient
import Events.AccountCreated

class OpenAccount(private var client: EventStoreDBClient)  {
    fun execute(uuid: String, type: String) : Boolean {
        val event = AccountCreated()
        event.uuid = uuid
        event.type = type

        client.appendToStream("account-${event.uuid}", event.toEventData()).get()

        return true
    }
}