package AccountsDomain.Commands

import Architecture.Command

class CloseAccount(var accountUuid : String = "", var corrolationId : String = "") : Command() {
    override fun name(): String { return "CloseAccount" }
}