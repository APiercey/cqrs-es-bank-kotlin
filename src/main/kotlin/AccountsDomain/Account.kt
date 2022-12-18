package AccountsDomain

import Architecture.Aggregate
import Architecture.DomainError
import Events.*
import AccountsDomain.Commands.*
import Architecture.BaseEvent

class Account() : Aggregate() {
    var uuid : String = ""
    var type : String = ""
    var open : Boolean = false
    var blocked : Boolean = false
    var currentLedgerUuid : String = ""
    var balance : Int = 0

    constructor(uuid: String = "", type: String = "", corrolationId : String = "") : this() {
        enqueue(AccountCreated(uuid, type, corrolationId))
    }

    fun handle(cmd : BlockAccount) {
        if(isClosed()) { throw DomainError("Account is closed", "") }
        if(isBlocked()) { return@handle }

        enqueue(AccountBlocked(uuid, cmd.corrolationId))
    }

    fun handle(cmd : UnblockAccount) {
        if(isClosed()) { throw DomainError("Account is closed!", "") }
        if(!isBlocked()) { return@handle }

        enqueue(AccountUnblocked(uuid, cmd.corrolationId))
    }

    fun handle(cmd : CloseAccount) {
        if(!open) { throw DomainError("Account Already Closed!", "") }

        enqueue(AccountClosed(uuid, cmd.corrolationId))
    }

    fun handle(cmd : WithdrawFunds) {
        if(isClosed()) { throw DomainError("Account is closed", cmd.corrolationId) }
        if((balance - cmd.amount) < 0) { throw DomainError("Not enough funds", cmd.corrolationId) }

        enqueue(FundsWithdrawn(uuid, cmd.amount, cmd.corrolationId))
    }

    fun handle(cmd : DepositFunds) {
        if(isClosed()) { throw DomainError("Account is closed", cmd.corrolationId) }
        enqueue(FundsDeposited(uuid, cmd.amount, cmd.corrolationId))
    }

    fun isClosed() : Boolean { return !open }

    fun isBlocked() : Boolean { return blocked }

    override fun apply(event: BaseEvent) {
        when(event) {
            is AccountCreated -> applyEvent(event)
            is AccountBlocked -> applyEvent(event)
            is AccountUnblocked -> applyEvent(event)
            is AccountClosed -> applyEvent(event)
            is FundsDeposited -> applyEvent(event)
            is FundsWithdrawn -> applyEvent(event)
            else -> null
        }
    }

    private fun applyEvent(event: AccountCreated) {
        uuid = event.uuid
        type = event.type
        open = true
        blocked = false
    }

    private fun applyEvent(event: AccountBlocked) {
        blocked = true
    }

    private fun applyEvent(event: AccountUnblocked) {
        blocked = false
    }

    private fun applyEvent(event: AccountClosed) {
        open = false
    }

    private fun applyEvent(event: FundsWithdrawn) {
        balance -= event.amount
    }

    private fun applyEvent(event: FundsDeposited) {
        balance += event.amount
    }
}