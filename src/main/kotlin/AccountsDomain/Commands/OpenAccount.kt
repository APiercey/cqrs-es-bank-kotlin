package AccountsDomain.Commands

import Architecture.Command

class OpenAccount(var accountUuid : String = "", var accountType : String = "", var corrolationId : String = "") : Command() {
    override fun name(): String { return "OpenAccount" }
}


