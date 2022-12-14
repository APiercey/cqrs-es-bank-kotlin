package LedgersDomain

import Helpers.AllReadPosition
import Helpers.allPositionRecorder
import com.eventstore.dbclient.*
import Events.AccountCreated
import LedgersDomain.Commands.OpenLedger
import org.litote.kmongo.*
import java.util.*
import AppTree

private const val SUBSCRIBER_NAME = "account-created-ledger-open"

fun startLedgerAccountCreatedSubscriber(appTree : AppTree) {
    val database = appTree.mongoDatabase()
    val mongoClient = appTree.mongoClient()
    val esClient = appTree.esClient()
    val allReadPositions = database.getCollection<AllReadPosition>()
    val recordPosition = allPositionRecorder(allReadPositions, SUBSCRIBER_NAME)
    val bus = appTree.bus()

    val listener: SubscriptionListener = object : SubscriptionListener() {
        val mongoSession = mongoClient.startSession()

        override fun onEvent(subscription: Subscription?, event: ResolvedEvent) {
            mongoSession.use { clientSession ->
                if(!event.originalEvent.eventType.startsWith("events.AccountCreated")) { return Unit }
                val originalEvent = event.originalEvent.getEventDataAs(AccountCreated::class.java)

                bus.send(OpenLedger(UUID.randomUUID().toString(), originalEvent.uuid))
                recordPosition(event)
            }
        }

        override fun onError(subscription: Subscription?, throwable: Throwable) {
            println("Subscription was dropped due to " + throwable.message)
        }
    }

    val position = allReadPositions.findOne(AllReadPosition::subscriberName eq SUBSCRIBER_NAME)

    val subscriberOptions = if(position == null) {
        SubscribeToAllOptions.get().fromStart()
    } else {
        SubscribeToAllOptions.get().fromPosition(Position(position.commitPosition, position.preparePosition))
    }

    esClient.subscribeToAll(
        listener,
        subscriberOptions
    )
}