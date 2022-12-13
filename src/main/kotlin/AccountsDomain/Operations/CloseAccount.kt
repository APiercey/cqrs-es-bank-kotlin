package AccountsDomain.Operations

import com.eventstore.dbclient.EventStoreDBClient
import AccountsDomain.Account

class CloseAccount(private var client: EventStoreDBClient) {
    fun execute(account: Account) : Boolean {
        account.close()
//        accountRepo.save(account)

        return true
    }
}