package AccountsDomain

import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.ReadStreamOptions
import com.eventstore.dbclient.ResolvedEvent
import Events.AccountBlocked
import Events.AccountClosed
import Events.AccountCreated
import Events.AccountUnblocked

class AccountRepo(private val client: EventStoreDBClient) {
    fun fetch(uuid: String): Account? {
        val account = Account()

        getEntityEvents("account-${uuid}").forEach {
            when (it.event.eventType) {
                "events.AccountCreated" -> account.apply(it.originalEvent.getEventDataAs(AccountCreated::class.java))
                "events.AccountBlocked" -> account.apply(it.originalEvent.getEventDataAs(AccountBlocked::class.java))
                "events.AccountUnblocked" -> account.apply(it.originalEvent.getEventDataAs(AccountUnblocked::class.java))
                "events.AccountClosed" -> account.apply(it.originalEvent.getEventDataAs(AccountClosed::class.java))
            }
        }

        if(account.uuid == "") {
            return null
        }

        return account
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
