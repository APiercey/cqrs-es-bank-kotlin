package Events

import Architecture.BaseEvent

class LedgerAssigned(var accountUuid: String = "", var ledgerUuid: String = "") : BaseEvent() {
    override fun eventType(): String { return "events.LedgerAssigned" }
}