package Events

import Architecture.BaseEvent

class AccountClosed(var uuid: String = "", var correlationId : String = "") : BaseEvent() {
    override fun eventType(): String { return "events.AccountClosed" }
}