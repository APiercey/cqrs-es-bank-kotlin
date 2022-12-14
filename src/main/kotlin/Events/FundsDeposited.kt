package Events

import Architecture.BaseEvent

class FundsDeposited(var ledgerUuid: String = "", var amount: Int = 0) : BaseEvent() {
    override fun eventType(): String { return "events.FundsDeposited" }
}