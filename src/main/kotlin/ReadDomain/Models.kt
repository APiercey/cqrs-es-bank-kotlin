package ReadDomain

data class ReadAccount(val uuid: String, val type: String, val blocked: Boolean, val open: Boolean, val balance: Int)
data class ReadLedger(val uuid: String, val accountUuid: String)
data class ReadTransaction(val uuid: String, val creditorUuid: String, val debtorUuid: String, val amount: Int, val status: String)