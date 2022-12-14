package LedgersDomain

import Events.*
import com.eventstore.dbclient.*


class LedgerRepo(private val client: EventStoreDBClient) {
    fun fetch(uuid: String): Ledger? {
        val ledger = Ledger()

        getEntityEvents("account-${uuid}").forEach {
            when (it.event.eventType) {
                "events.LedgerOpened" -> ledger.apply(it.originalEvent.getEventDataAs(LedgerOpened::class.java))
                "events.FundsDeposited" -> ledger.apply(it.originalEvent.getEventDataAs(FundsDeposited::class.java))
                "events.FundsWithdrawn" -> ledger.apply(it.originalEvent.getEventDataAs(FundsWithdrawn::class.java))
            }
        }

        if(ledger.uuid == "") { return null }

        return ledger
    }

    fun save(ledger : Ledger) {
        val eventsIterator : MutableIterator<EventData> = ledger
            .getMutations()
            .map { it.toEventData() }
            .toMutableList()
            .iterator()

        // TODO: Make this use optimistic locking
        val options = AppendToStreamOptions
            .get()
            .expectedRevision(ExpectedRevision.any())

        client.appendToStream("ledger-${ledger.uuid}", options, eventsIterator).get()
        ledger.clearMutations()
    }

    private fun getEntityEvents(streamId: String): List<ResolvedEvent> {
        val options = ReadStreamOptions.get()
            .forwards()
            .fromStart()

        return this.client
            .readStream(streamId, options)
            .get()
            .events
    }
}
