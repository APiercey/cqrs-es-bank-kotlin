package AccountsDomain

import AccountsDomain.Commands.*

class CommandHandler(val accountRepo : AccountRepo) {
    fun handle(cmd: OpenAccount) {
        val account = Account(cmd.accountUuid, cmd.accountType, cmd.correlationId)
        accountRepo.save(account)
    }

    fun handle(cmd: BlockAccount) {
        val account = fetchAccount(cmd.accountUuid)
        account.handle(cmd)
        accountRepo.save(account)
    }

    fun handle(cmd: UnblockAccount) {
        val account = fetchAccount(cmd.accountUuid)
        account.handle(cmd)
        accountRepo.save(account)
    }

    fun handle(cmd: CloseAccount) {
        val account = fetchAccount(cmd.accountUuid)
        account.handle(cmd)
        accountRepo.save(account)
    }

    fun handle(cmd: WithdrawFunds) {
        val account = fetchAccount(cmd.accountUuid)
        account.handle(cmd)
        accountRepo.save(account)
    }

    fun handle(cmd: DepositFunds) {
        val account = fetchAccount(cmd.accountUuid)
        account.handle(cmd)
        accountRepo.save(account)
    }

    private fun fetchAccount(uuid : String) : Account {
        return accountRepo.fetch(uuid) ?: throw Exception("Account not found")
    }
}