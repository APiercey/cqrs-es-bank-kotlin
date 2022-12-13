package AccountsDomain.Operations

import com.eventstore.dbclient.EventStoreDBClient
import AccountsDomain.AccountRepo

class AssignLedger(private var client: EventStoreDBClient, private var accountRepo : AccountRepo) {
    fun execute(accountUuid: String, ledgerUuid: String) : Boolean {
        val account = accountRepo.fetch(accountUuid) ?: throw Exception("Account not found")

        account.assignLedger(ledgerUuid)

        accountRepo.save(account)

        return true
    }
}