package Events

class LedgerAssigned(var accountUuid: String = "", var ledgerUuid: String = "") : BaseEvent() {
    override fun eventType(): String { return "events.LedgerAssigned" }
}