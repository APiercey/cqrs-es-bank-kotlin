package Events

import Architecture.BaseEvent

class TransactionCompleted(var uuid: String = "", var correlationId : String = "") : BaseEvent() {
    override fun eventType(): String { return "events.TransactionCompleted" }
}