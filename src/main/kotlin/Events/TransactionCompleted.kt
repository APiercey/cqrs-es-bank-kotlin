package Events

import Architecture.BaseEvent

class TransactionCompleted(var uuid: String = "") : BaseEvent() {
    override fun eventType(): String { return "events.TransactionCompleted" }
}