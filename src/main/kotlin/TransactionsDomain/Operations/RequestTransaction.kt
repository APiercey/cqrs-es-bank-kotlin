package TransactionsDomain.Operations

import com.eventstore.dbclient.EventStoreDBClient
import Events.TransactionRequested

class RequestTransaction(private var client: EventStoreDBClient) {
    fun execute(uuid: String, creditorUuid: String, debtorUuid: String, amount: Int) : Boolean {
        val event = TransactionRequested()
        event.uuid = uuid
        event.creditorUuid = creditorUuid
        event.debtorUuid = debtorUuid
        event.amount = amount

        client.appendToStream("transactions-${event.uuid}", event.toEventData()).get()

        return true
    }
}