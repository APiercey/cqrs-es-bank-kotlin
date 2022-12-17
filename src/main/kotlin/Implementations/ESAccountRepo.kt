package Implementations

import Events.deserialize
import AccountsDomain.Account
import AccountsDomain.AccountRepo
import com.eventstore.dbclient.*

class ESAccountRepo(private val client: EventStoreDBClient) : AccountRepo {
    override fun fetch(uuid: String): Account? {
        val account = Account()

        getEntityEvents("account-${uuid}")
            .map { deserialize(it) }
            .forEach { account.apply(it) }

        if(account.uuid == "") {
            return null
        }

        return account
    }

    override fun save(account : Account) {
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
