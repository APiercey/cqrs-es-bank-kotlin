package TransactionsDomain

import AccountsDomain.buildCatchupSubscriber
import AppTree
import Events.*

fun startAccountTransferEventsHandler(appTree: AppTree) {
    val sagaRepo = appTree.sagaRepo()

    buildCatchupSubscriber(
        appTree.esClient(),
        appTree.mongoClient(),
        "account-transfer-saga-event-handler"
    ) { event ->
        if(event.originalEvent.streamId.startsWith("saga-")) { return@buildCatchupSubscriber }

        when (event.originalEvent.eventType) {
            "events.TransactionRequested" -> {
                val originalEvent = event.originalEvent.getEventDataAs(TransactionRequested::class.java)
                val saga = AccountTransferSaga()

                saga.transition(originalEvent)
                sagaRepo.save(saga)
            }
            "events.FundsWithdrawn" -> {
                val originalEvent = event.originalEvent.getEventDataAs(FundsWithdrawn::class.java)
                val saga = sagaRepo.fetch(originalEvent.corrolationId) ?: return@buildCatchupSubscriber

                saga.transition(originalEvent)
                sagaRepo.save(saga)
            }
            "events.FundsDeposited" -> {
                val originalEvent = event.originalEvent.getEventDataAs(FundsDeposited::class.java)
                val saga = sagaRepo.fetch(originalEvent.corrolationId) ?: return@buildCatchupSubscriber

                saga.transition(originalEvent)
                sagaRepo.save(saga)
            }
            else -> null
        }
    }
}