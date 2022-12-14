package Events

import Architecture.BaseEvent

class LedgerOpened(var uuid: String = "", var accountUuid: String = "") : BaseEvent() {
    override fun eventType(): String { return "events.LedgerOpened" }
}