package AccountsDomain.Operations

import com.eventstore.dbclient.EventStoreDBClient
import Events.AccountUnblocked

class UnblockAccount(private var client: EventStoreDBClient) {
    fun execute(uuid: String) : Boolean {
        val event = AccountUnblocked()
        event.uuid = uuid

        client.appendToStream("account-${event.uuid}", event.toEventData()).get()

        return true
    }
}