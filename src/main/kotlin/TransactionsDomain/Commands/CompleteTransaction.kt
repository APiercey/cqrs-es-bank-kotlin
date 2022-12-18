package TransactionsDomain.Commands

import Architecture.Command

class CompleteTransaction(var transactionUuid : String = "", var corrolationId : String = "") : Command() {
    override fun name(): String { return "CompleteTransaction" }
}