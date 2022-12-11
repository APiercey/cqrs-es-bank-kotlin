package readDomain

data class ReadAccount(val uuid: String, val type: String, val blocked: Boolean, val open: Boolean)
data class ReadLedger(val uuid: String, val accountUuid: String)