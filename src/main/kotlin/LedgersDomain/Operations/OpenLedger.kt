package LedgersDomain.Operations

import com.eventstore.dbclient.EventStoreDBClient
import Events.LedgerOpened

class OpenLedger(private var client: EventStoreDBClient) {
    fun execute(uuid: String, accountUuid: String) : Boolean {
        val event = LedgerOpened()
        event.uuid = uuid
        event.accountUuid = accountUuid

        client.appendToStream("ledger-${event.uuid}", event.toEventData()).get()

        return true
    }
}