package AccountsDomain

interface AccountRepo {
    fun fetch(uuid: String): Account?
    fun save(account : Account)
}
