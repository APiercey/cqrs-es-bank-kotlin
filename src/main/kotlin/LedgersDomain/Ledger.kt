package LedgersDomain


import Architecture.Aggregate
import Architecture.BaseEvent
import Events.*
import LedgersDomain.Commands.*

class Ledger() : Aggregate() {
    var uuid : String = ""
    var accountUuid : String = ""
    var balance : Int = 0

    constructor(uuid: String = "", accountUuid: String = "") : this() {
        enqueue(LedgerOpened(uuid, accountUuid))
    }

    fun handle(cmd : WithdrawFunds) {
        if((balance - cmd.amount) < 0) { throw Exception("Not enough funds") }

        enqueue(FundsWithdrawn(uuid, cmd.amount))
    }

    fun handle(cmd : DepositFunds) {
        enqueue(FundsDeposited(uuid, cmd.amount))
    }

    override fun apply(event: BaseEvent) {
        when(event) {
            is LedgerOpened -> applyEvent(event)
            else -> null
        }
    }

    private fun applyEvent(event: LedgerOpened) {
        uuid = event.uuid
        accountUuid = event.accountUuid
        balance = 0
    }
}