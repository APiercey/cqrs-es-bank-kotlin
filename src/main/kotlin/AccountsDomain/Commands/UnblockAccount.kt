package AccountsDomain.Commands

import Architecture.Command

class UnblockAccount(var accountUuid : String = "", var correlationId : String = "") : Command() {
    override fun name(): String { return "UnblockAccount" }
    override fun correlationId(): String { return correlationId }
}