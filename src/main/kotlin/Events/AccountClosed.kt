package Events

import Architecture.BaseEvent

class AccountClosed(var uuid: String = "") : BaseEvent() {
    override fun eventType(): String { return "events.AccountClosed" }
}