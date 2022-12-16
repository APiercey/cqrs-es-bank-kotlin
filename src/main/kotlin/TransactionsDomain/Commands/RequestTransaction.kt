package TransactionsDomain.Commands

import Architecture.Command

class RequestTransaction(var transactionUuid : String = "", var senderUuid : String = "", var receiverUuid : String = "", var amount : Int = 0) : Command() {
    override fun name(): String { return "RequestTransaction" }
}