package TransactionsDomain.Commands

import Architecture.Command

class CompleteTransaction(var transactionUuid : String = "", var correlationId : String = "") : Command() {
    override fun name(): String { return "CompleteTransaction" }
    override fun correlationId(): String { return correlationId }
}