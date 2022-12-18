package AccountsDomain.Commands

import Architecture.Command

class CloseAccount(var accountUuid : String = "", var correlationId : String = "") : Command() {
    override fun name(): String { return "CloseAccount" }
    override fun correlationId(): String { return correlationId }
}