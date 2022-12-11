package events

class LedgerOpened(var uuid: String = "", var accountUuid: String = "") : BaseEvent() {
    override fun eventType(): String { return "events.LedgerOpened" }
}