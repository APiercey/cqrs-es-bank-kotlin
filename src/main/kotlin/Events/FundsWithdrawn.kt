package Events

import Architecture.BaseEvent

class FundsWithdrawn(var ledgerUuid: String = "", var amount: Int = 0) : BaseEvent() {
    override fun eventType(): String { return "events.FundsWithdrawn" }
}