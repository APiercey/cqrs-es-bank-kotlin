package Events

import Architecture.BaseEvent

class AccountUnblocked(var uuid: String = "") : BaseEvent() {
    override fun eventType(): String { return "events.AccountUnblocked" }
}