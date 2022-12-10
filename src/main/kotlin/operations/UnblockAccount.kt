package operations

import com.eventstore.dbclient.EventStoreDBClient
import events.AccountUnblocked

class UnblockAccount(private var client: EventStoreDBClient) : BaseOperation() {
    fun execute(uuid: String) : Boolean {
        val event = AccountUnblocked()
        event.uuid = uuid

        client.appendToStream("account-${event.uuid}", buildEventData(event)).get()

        return true
    }
}