package Events

class AccountBlocked(var uuid: String = "") : BaseEvent() {
    override fun eventType(): String { return "events.AccountBlocked" }
}