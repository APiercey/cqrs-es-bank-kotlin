package TransactionsDomain

import Events.*
import com.eventstore.dbclient.*


class TransactionRepo(private val client: EventStoreDBClient) {
    fun fetch(uuid: String): Transaction? {
        val transaction = Transaction()

        getEntityEvents("transaction-${uuid}").forEach {
            when (it.event.eventType) {
                "events.TransactionRequested" -> transaction.apply(it.originalEvent.getEventDataAs(TransactionRequested::class.java))
            }
        }

        if(transaction.uuid == "") {
            return null
        }

        return transaction
    }

    fun save(transaction : Transaction) {
        val eventsIterator : MutableIterator<EventData> = transaction
            .getMutations()
            .map { it.toEventData() }
            .toMutableList()
            .iterator()

        // TODO: Make this use optimistic locking
        val options = AppendToStreamOptions
            .get()
            .expectedRevision(ExpectedRevision.any())

        client.appendToStream("transaction-${transaction.uuid}", options, eventsIterator).get()
        transaction.clearMutations()
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
