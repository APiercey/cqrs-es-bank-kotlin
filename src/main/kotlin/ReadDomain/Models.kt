package ReadDomain

data class ReadAccount(val uuid: String, val type: String, val blocked: Boolean, val open: Boolean, val balance: Int)
data class ReadTransaction(val uuid: String, val senderUuid: String, val receiverUuid: String, val amount: Int, val status: String)