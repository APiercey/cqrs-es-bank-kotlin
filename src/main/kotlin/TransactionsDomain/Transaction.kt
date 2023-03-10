package TransactionsDomain

import Architecture.Aggregate
import Architecture.BaseEvent
import Architecture.DomainError
import Events.*
import TransactionsDomain.Commands.*

class Transaction() : Aggregate() {
    var uuid : String = ""
    var senderUuid : String = ""
    var receiverUuid : String = ""
    var amount : Int = 0
    var status : String = ""

    constructor(uuid: String = "", senderUuid: String = "", receiverUuid: String = "", amount: Int = 0, correlationId : String = "") : this() {
        enqueue(TransactionRequested(uuid, senderUuid, receiverUuid, amount, correlationId))
    }

    fun handle(cmd : CompleteTransaction) {
        if(isCompleted()) { throw DomainError("Transaction already completed!", cmd.correlationId) }

        enqueue(TransactionCompleted(uuid, cmd.correlationId))
    }

    fun handle(cmd : FailTransaction) {
        if(isCompleted()) { throw DomainError("Transaction already completed!", cmd.correlationId) }

        enqueue(TransactionFailed(uuid, cmd.correlationId))
    }

    override fun apply(event: BaseEvent) {
        when(event) {
            is TransactionRequested -> applyEvent(event)
            is TransactionCompleted -> applyEvent(event)
            is TransactionFailed -> applyEvent(event)
        }
    }

    private fun applyEvent(event: TransactionRequested) {
        uuid = event.uuid
        senderUuid = event.senderUuid
        receiverUuid = event.receiverUuid
        amount = event.amount
        status = "PENDING"
    }

    private fun applyEvent(event: TransactionCompleted) {
        status = "COMPLETED"
    }

    private fun applyEvent(event: TransactionFailed) {
        status = "FAILED"
    }

    private fun isCompleted() : Boolean {
        return status == "COMPLETED"
    }
}