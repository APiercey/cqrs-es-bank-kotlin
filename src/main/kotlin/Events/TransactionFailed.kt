package Events

import Architecture.BaseEvent

class TransactionFailed(var uuid: String = "", var corrolationId : String = "") : BaseEvent() {
    override fun eventType(): String { return "events.TransactionFailed" }
}