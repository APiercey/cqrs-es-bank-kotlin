package Events

import Architecture.BaseEvent

class AccountCreated(var uuid: String = "", var type: String = "", var correlationId : String = "") : BaseEvent() {
    override fun eventType(): String { return "events.AccountCreated" }
}