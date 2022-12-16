package Events

import Architecture.BaseEvent

class FundsDeposited(var accountUuid: String = "", var amount: Int = 0, var corrolationId : String = "") : BaseEvent() {
    override fun eventType(): String { return "events.FundsDeposited" }
}