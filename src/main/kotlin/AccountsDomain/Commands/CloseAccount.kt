package AccountsDomain.Commands

import Commands.Command

class CloseAccount(var accountUuid : String = "") : Command() {
    override fun name(): String { return "CloseAccount" }
}