package AccountsDomain.Commands

import Architecture.Command

class BlockAccount(var accountUuid : String = "", var corrolationId : String = "") : Command() {
    override fun name(): String { return "BlockAccount" }
}