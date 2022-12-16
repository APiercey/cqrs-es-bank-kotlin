package TransactionsDomain.AccountTransfer

import Architecture.Bus
import Events.*
import com.eventstore.dbclient.*


class AccountTransferSagaRepo(private val client: EventStoreDBClient, private val bus : Bus) {
    fun fetch(uuid: String): AccountTransferSaga? {
        val saga = AccountTransferSaga()

        getEntityEvents("saga-${uuid}").forEach {
            when (it.event.eventType) {
                "events.TransactionRequested" -> saga.transition(it.originalEvent.getEventDataAs(TransactionRequested::class.java))
            }
        }

        if(saga.uuid == "") {
            return null
        }

        saga.clearUndispatchedCommands()
        saga.clearUncomittedEvents()

        return saga
    }

    fun save(saga : AccountTransferSaga) {
        val eventsIterator : MutableIterator<EventData> = saga
            .uncomittedEvents()
            .map { it.toEventData() }
            .toMutableList()
            .iterator()

        // TODO: Make this use optimistic locking
        val options = AppendToStreamOptions
            .get()
            .expectedRevision(ExpectedRevision.any())

        client.appendToStream("saga-${saga.uuid}", options, eventsIterator).get()

        saga.undispatchedCommands()
            .forEach() { bus.send(it) }

        saga.clearUndispatchedCommands()
        saga.clearUncomittedEvents()
    }

    private fun getEntityEvents(streamId: String): List<ResolvedEvent> {
        try {
            val options = ReadStreamOptions.get()
                .forwards()
                .fromStart()

            return this.client
                .readStream(streamId, options)
                .get()
                .events
        } catch(e : Exception) {
            val innerException: Throwable? = e.cause

            if (innerException is StreamNotFoundException) {
                return listOf()
            } else {
                throw e
            }
        }
    }
}
