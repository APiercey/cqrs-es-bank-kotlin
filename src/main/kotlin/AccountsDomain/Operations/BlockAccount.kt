package AccountsDomain.Operations

import com.eventstore.dbclient.EventStoreDBClient
import Events.AccountBlocked

class BlockAccount(private var client: EventStoreDBClient) {
    fun execute(uuid: String) : Boolean {
        val event = AccountBlocked()
        event.uuid = uuid

        client.appendToStream("account-${event.uuid}", event.toEventData()).get()

        return true
    }
}