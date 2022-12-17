package TransactionsDomain

interface TransactionRepo {
    fun fetch(uuid: String): Transaction?
    fun save(transaction : Transaction)
}
