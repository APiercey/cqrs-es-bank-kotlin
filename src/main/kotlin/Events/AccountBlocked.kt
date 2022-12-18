package Events

import Architecture.BaseEvent

class AccountBlocked(var uuid: String = "", var correlationId : String = "") : BaseEvent() {
    override fun eventType(): String { return "events.AccountBlocked" }
}