package AccountsDomain.Commands

import Architecture.Command

class DepositFunds(var accountUuid : String = "", var amount : Int = 0, var correlationId : String = "") : Command() {
    override fun name(): String { return "DepositFunds" }
    override fun correlationId(): String { return correlationId }
}