package operations

import com.eventstore.dbclient.EventStoreDBClient
import events.AccountCreated

class OpenAccount(private var client: EventStoreDBClient) : BaseOperation() {
    fun execute(uuid: String, type: String) : Boolean {
        val event = AccountCreated()
        event.uuid = uuid
        event.type = type

        client.appendToStream("account-${event.uuid}", buildEventData(event)).get()

        return true
    }
}