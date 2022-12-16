package Events

import Architecture.BaseEvent

class FundsWithdrawn(var accountUuid: String = "", var amount: Int = 0, var corrolationId : String = "") : BaseEvent() {
    override fun eventType(): String { return "events.FundsWithdrawn" }
}