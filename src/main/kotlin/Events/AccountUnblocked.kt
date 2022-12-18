package Events

import Architecture.BaseEvent

class AccountUnblocked(var uuid: String = "", var corrolationId : String = "") : BaseEvent() {
    override fun eventType(): String { return "events.AccountUnblocked" }
}