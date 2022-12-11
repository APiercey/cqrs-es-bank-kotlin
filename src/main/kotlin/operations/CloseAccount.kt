package operations

import com.eventstore.dbclient.EventStoreDBClient
import events.AccountClosed
import writeDomain.Account

class CloseAccount(private var client: EventStoreDBClient) : BaseOperation() {
    fun execute(account: Account) : Boolean {
        account.close()
                .forEach {
                    client.appendToStream(
                            "account-${account.uuid}",
                            buildEventData(it)
                        ).get()
                }

        return true
    }
}