package TransactionsDomain.Commands

import Architecture.Command

class RequestTransaction(var transactionUuid : String, var creditorUuid : String, var debtorUuid : String, var amount : Int) : Command() {
    override fun name(): String { return "RequestTransaction" }
}