package AccountsDomain.Commands

import Architecture.Command

class DepositFunds(var accountUuid : String = "", var amount : Int = 0, var corrolationId : String = "") : Command() {
    override fun name(): String { return "DepositFunds" }
}