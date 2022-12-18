package AccountsDomain.Commands

import Architecture.Command

class BlockAccount(var accountUuid : String = "", var correlationId : String = "") : Command() {
    override fun name(): String { return "BlockAccount" }
    override fun correlationId(): String { return correlationId }
}