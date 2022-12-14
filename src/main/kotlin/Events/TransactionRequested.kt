package Events

import Architecture.BaseEvent

class TransactionRequested(var uuid: String = "", var creditorUuid: String = "", var debtorUuid: String = "", var amount: Int = 0) : BaseEvent() {
    override fun eventType(): String { return "events.TransactionRequested" }
}