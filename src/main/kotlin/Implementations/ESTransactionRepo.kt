package Implementations

import Events.*
import TransactionsDomain.Transaction
import TransactionsDomain.TransactionRepo
import com.eventstore.dbclient.*

class ESTransactionRepo(private val client: EventStoreDBClient) : TransactionRepo {
    override fun fetch(uuid: String): Transaction? {
        val transaction = Transaction()

        getEntityEvents("transaction-${uuid}")
            .map { deserialize(it) }
            .forEach { transaction.apply(it) }

        if(transaction.uuid == "") {
            return null
        }

        return transaction
    }

    override fun save(transaction : Transaction) {
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
