package Events

import Architecture.BaseEvent

class TransactionCompleted(var uuid: String = "", var corrolationId : String = "") : BaseEvent() {
    override fun eventType(): String { return "events.TransactionCompleted" }
}