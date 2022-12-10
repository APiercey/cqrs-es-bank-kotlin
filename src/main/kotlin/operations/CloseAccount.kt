package operations

import com.eventstore.dbclient.EventStoreDBClient
import events.AccountClosed

class CloseAccount(private var client: EventStoreDBClient) : BaseOperation() {
    fun execute(uuid: String) : Boolean {
        val event = AccountClosed()
        event.uuid = uuid

        client.appendToStream("account-${event.uuid}", buildEventData(event)).get()

        return true
    }
}