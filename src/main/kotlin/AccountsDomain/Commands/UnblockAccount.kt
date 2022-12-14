package AccountsDomain.Commands

import Architecture.Command

class UnblockAccount(var accountUuid : String = "") : Command() {
    override fun name(): String { return "UnblockAccount" }
}