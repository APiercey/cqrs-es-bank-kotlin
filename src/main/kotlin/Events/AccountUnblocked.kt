package Events

import Architecture.BaseEvent

class AccountUnblocked(var uuid: String = "", var correlationId : String = "") : BaseEvent() {
    override fun eventType(): String { return "events.AccountUnblocked" }
}