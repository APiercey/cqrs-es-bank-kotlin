package AccountsDomain.Operations

import com.eventstore.dbclient.EventStoreDBClient
import AccountsDomain.Account

class CloseAccount(private var client: EventStoreDBClient) {
    fun execute(account: Account) : Boolean {
        account.close()
                .forEach {
                    client.appendToStream(
                            "account-${account.uuid}",
                        it.toEventData()
                        ).get()
                }

        return true
    }
}