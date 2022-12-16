package TransactionsDomain.Commands

import Architecture.Command

class CompleteTransaction(var transactionUuid : String = "") : Command() {
    override fun name(): String { return "CompleteTransaction" }
}