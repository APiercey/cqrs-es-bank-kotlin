package AccountsDomain

import Events.AccountBlocked
import Events.AccountClosed
import Events.AccountCreated
import Events.AccountUnblocked

class Account() {
    var uuid: String = ""
    var type: String = ""
    var blocked: Boolean = false
    var open: Boolean = false

    fun apply(event: AccountCreated) {
        uuid = event.uuid
        type = event.type
        open = true
        blocked = false
    }

    fun close() : List<Events.BaseEvent> {
        if(!open) { throw Exception("Account Already Closed!") }

        return listOf(AccountClosed(uuid))
    }

    fun apply(event: AccountBlocked) {
        blocked = true
    }

    fun apply(event: AccountUnblocked) {
        blocked = false
    }

    fun apply(event: AccountClosed) {
        open = false
    }
}