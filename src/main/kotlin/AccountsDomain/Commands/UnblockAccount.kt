package AccountsDomain.Commands

import Commands.Command

class UnblockAccount(var accountUuid : String = "") : Command() {
    override fun name(): String { return "UnblockAccount" }
}