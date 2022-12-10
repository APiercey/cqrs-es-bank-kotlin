package events

class AccountCreated(var uuid: String = "", var type: String = "") : BaseEvent() {
    override fun eventType(): String { return "events.AccountCreated" }
}