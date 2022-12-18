package Events

import Architecture.BaseEvent

class TransactionRequested(var uuid: String = "", var senderUuid: String = "", var receiverUuid: String = "", var amount: Int = 0, var correlationId : String = "") : BaseEvent() {
    override fun eventType(): String { return "events.TransactionRequested" }
}