package Architecture

class CapturedDomainError(var message : String = "", var correlationId : String = "", var originalCommandName : String = "") : BaseEvent() {
    override fun eventType(): String { return "CapturedDomainError" }
}