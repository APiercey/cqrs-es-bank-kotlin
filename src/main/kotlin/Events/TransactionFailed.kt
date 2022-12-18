package Events

import Architecture.BaseEvent

class TransactionFailed(var uuid: String = "", var correlationId : String = "") : BaseEvent() {
    override fun eventType(): String { return "events.TransactionFailed" }
}