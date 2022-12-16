package TransactionsDomain

import Architecture.Aggregate
import Architecture.BaseEvent
import Events.*
import TransactionsDomain.Commands.*

class Transaction() : Aggregate() {
    var uuid : String = ""
    var senderUuid : String = ""
    var receiverUuid : String = ""
    var amount : Int = 0
    var status : String = ""

    constructor(uuid: String = "", senderUuid: String = "", receiverUuid: String = "", amount: Int = 0) : this() {
        enqueue(TransactionRequested(uuid, senderUuid, receiverUuid, amount))
    }

    fun handle(cmd : CompleteTransaction) {
        if(!isCompleted()) { throw Exception("Transaction already completed!") }

        enqueue(TransactionCompleted(uuid))
    }

    override fun apply(event: BaseEvent) {
        when(event) {
            is TransactionRequested -> applyEvent(event)
            is TransactionCompleted -> applyEvent(event)
            else -> null
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

    private fun isCompleted() : Boolean {
        return status == "COMPLETED"
    }
}