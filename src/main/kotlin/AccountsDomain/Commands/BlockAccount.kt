package AccountsDomain.Commands

import Architecture.Command

class BlockAccount(var accountUuid : String = "") : Command() {
    override fun name(): String { return "BlockAccount" }
}