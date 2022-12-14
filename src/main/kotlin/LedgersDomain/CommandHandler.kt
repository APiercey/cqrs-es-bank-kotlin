package LedgersDomain

import LedgersDomain.Commands.*

class CommandHandler(val ledgerRepo : LedgerRepo) {
    fun handle(cmd: OpenLedger) {
        val ledger = Ledger(cmd.ledgerUuid, cmd.accountUuid)
        ledgerRepo.save(ledger)
    }

    fun handle(cmd: WithdrawFunds) {
        val account = fetchLedger(cmd.ledgerUuid)
        account.handle(cmd)
        ledgerRepo.save(account)
    }

    fun handle(cmd: DepositFunds) {
        val account = fetchLedger(cmd.ledgerUuid)
        account.handle(cmd)
        ledgerRepo.save(account)
    }

    private fun fetchLedger(uuid : String) : Ledger {
        return ledgerRepo.fetch(uuid) ?: throw Exception("Ledger not found")
    }
}