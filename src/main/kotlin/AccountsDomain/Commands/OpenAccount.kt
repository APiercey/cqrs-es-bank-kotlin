package AccountsDomain.Commands

import Architecture.Command

class OpenAccount(var accountUuid : String = "", var accountType : String = "", var correlationId : String = "") : Command() {
    override fun name(): String { return "OpenAccount" }
    override fun correlationId(): String { return correlationId }
}


