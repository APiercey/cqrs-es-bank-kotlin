package AccountsDomain.Commands

import Architecture.Command

class WithdrawFunds(var accountUuid : String = "", var amount : Int = 0, var corrolationId : String = "") : Command() {
    override fun name(): String { return "WithdrawFunds" }
}