package AccountsDomain.Commands

import Commands.Command

class BlockAccount(var accountUuid : String = "") : Command() {
    override fun name(): String { return "BlockAccount" }
}