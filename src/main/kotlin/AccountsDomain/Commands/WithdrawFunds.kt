package AccountsDomain.Commands

import Architecture.Command

class WithdrawFunds(var accountUuid : String = "", var amount : Int = 0, var correlationId : String = "") : Command() {
    override fun name(): String { return "WithdrawFunds" }
    override fun correlationId(): String { return correlationId }
}