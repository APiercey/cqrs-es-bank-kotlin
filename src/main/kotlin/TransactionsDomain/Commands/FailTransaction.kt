package TransactionsDomain.Commands

import Architecture.Command

class FailTransaction(var transactionUuid : String = "", var correlationId : String = "") : Command() {
    override fun name(): String { return "FailTransaction" }
    override fun correlationId(): String { return correlationId }
}