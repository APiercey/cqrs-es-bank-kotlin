package Events

import com.eventstore.dbclient.ResolvedEvent
import Architecture.BaseEvent

fun deserialize(event : ResolvedEvent) : BaseEvent {
    return when (event.event.eventType) {
        "events.AccountCreated" -> event.originalEvent.getEventDataAs(AccountCreated::class.java)
        "events.AccountBlocked" -> event.originalEvent.getEventDataAs(AccountBlocked::class.java)
        "events.AccountUnblocked" -> event.originalEvent.getEventDataAs(AccountUnblocked::class.java)
        "events.AccountClosed" -> event.originalEvent.getEventDataAs(AccountClosed::class.java)
        "events.FundsDeposited" -> event.originalEvent.getEventDataAs(FundsDeposited::class.java)
        "events.FundsWithdrawn" -> event.originalEvent.getEventDataAs(FundsWithdrawn::class.java)
        "events.TransactionRequested" -> event.originalEvent.getEventDataAs(TransactionRequested::class.java)
        "events.TransactionCompleted" -> event.originalEvent.getEventDataAs(TransactionCompleted::class.java)
        "events.TransactionFailed" -> event.originalEvent.getEventDataAs(TransactionFailed::class.java)
        else -> throw Exception("Unknown Event")
    }
}