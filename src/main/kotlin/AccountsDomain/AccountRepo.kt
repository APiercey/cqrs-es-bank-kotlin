package AccountsDomain

import Events.*
import com.eventstore.dbclient.*

class AccountRepo(private val client: EventStoreDBClient) {
    fun fetch(uuid: String): Account? {
        val account = Account()

        getEntityEvents("account-${uuid}").forEach {
            when (it.event.eventType) {
                "events.AccountCreated" -> account.apply(it.originalEvent.getEventDataAs(AccountCreated::class.java))
                "events.AccountBlocked" -> account.apply(it.originalEvent.getEventDataAs(AccountBlocked::class.java))
                "events.AccountUnblocked" -> account.apply(it.originalEvent.getEventDataAs(AccountUnblocked::class.java))
                "events.AccountClosed" -> account.apply(it.originalEvent.getEventDataAs(AccountClosed::class.java))
                "events.FundsDeposited" -> account.apply(it.originalEvent.getEventDataAs(FundsDeposited::class.java))
                "events.FundsWithdrawn" -> account.apply(it.originalEvent.getEventDataAs(FundsWithdrawn::class.java))
            }
        }

        if(account.uuid == "") {
            return null
        }

        return account
    }

    fun save(account : Account) {
        val eventsIterator : MutableIterator<EventData> = account
            .getMutations()
            .map { it.toEventData() }
            .toMutableList()
            .iterator()

        // TODO: Make this use optimistic locking
        val options = AppendToStreamOptions
            .get()
            .expectedRevision(ExpectedRevision.any())

        client.appendToStream("account-${account.uuid}", options, eventsIterator).get()
        account.clearMutations()
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
