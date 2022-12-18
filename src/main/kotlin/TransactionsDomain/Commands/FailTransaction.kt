package TransactionsDomain.Commands

import Architecture.Command

class FailTransaction(var transactionUuid : String = "", var corrolationId : String = "") : Command() {
    override fun name(): String { return "FailTransaction" }
}