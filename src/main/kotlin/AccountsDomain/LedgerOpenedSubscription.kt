package AccountsDomain

import com.eventstore.dbclient.ResolvedEvent
import Events.LedgerOpened
import AccountsDomain.Commands.AssignLedger
import AppTree

private const val SUBSCRIBER_NAME = "ledger-opened-ledger-assign"

fun startLedgerOpenedAssignLedgerSubscriber(appTree : AppTree) {
    buildCatchupSubscriber(appTree.esClient(), appTree.mongoClient(), SUBSCRIBER_NAME) { event: ResolvedEvent ->
        if(!event.originalEvent.eventType.startsWith("events.LedgerOpened")) { return@buildCatchupSubscriber }

        val originalEvent = event.originalEvent.getEventDataAs(LedgerOpened::class.java)

        appTree.bus().send(AssignLedger(originalEvent.accountUuid, originalEvent.uuid))
    }
}