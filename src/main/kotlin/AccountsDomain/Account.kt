package AccountsDomain

import Aggregate
import Events.*
import AccountsDomain.Commands.*
import io.ktor.websocket.*

class Account() : Aggregate() {
    var uuid : String = ""
    var type : String = ""
    var open : Boolean = false
    var blocked : Boolean = false
    var currentLedgerUuid : String = ""

    constructor(uuid: String = "", type: String = "") : this() {
        enqueue(AccountCreated(uuid, type))
    }

    fun handle(cmd : AssignLedger) {
        if(isClosed()) { throw Exception("Account is closed") }
        if(hasLedgerAssigned()) { throw Exception("Account already has a ledger") }

        enqueue(LedgerAssigned(uuid, cmd.ledgerUuid))
    }

    fun handle(cmd : BlockAccount) {
        if(isClosed()) { throw Exception("Account is closed") }
        if(isBlocked()) { return@handle }

        enqueue(AccountBlocked(uuid))
    }

    fun handle(cmd : UnblockAccount) {
        if(isClosed()) { throw Exception("Account is closed!") }
        if(!isBlocked()) { return@handle }

        enqueue(AccountUnblocked(uuid))
    }

    fun handle(cmd : CloseAccount) {
        if(!open) { throw Exception("Account Already Closed!") }

        enqueue(AccountClosed(uuid))
    }

    fun isClosed() : Boolean { return !open }

    fun isBlocked() : Boolean { return blocked }

    fun hasLedgerAssigned() : Boolean { return currentLedgerUuid != "" }

    override fun apply(event: BaseEvent) {
        when(event) {
            is AccountCreated -> applyEvent(event)
            is AccountBlocked -> applyEvent(event)
            is AccountUnblocked -> applyEvent(event)
            is AccountClosed -> applyEvent(event)
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

    private fun applyEvent(event: LedgerAssigned) {
        currentLedgerUuid = event.ledgerUuid
    }
}