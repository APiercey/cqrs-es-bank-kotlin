package operations

import com.eventstore.dbclient.EventStoreDBClient
import events.AccountBlocked

class BlockAccount(private var client: EventStoreDBClient) : BaseOperation() {
    fun execute(uuid: String) : Boolean {
        val event = AccountBlocked()
        event.uuid = uuid

        client.appendToStream("account-${event.uuid}", buildEventData(event)).get()

        return true
    }
}