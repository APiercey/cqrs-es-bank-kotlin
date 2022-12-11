package ReadDomain

import Helpers.AllReadPosition
import Helpers.allPositionRecorder
import com.eventstore.dbclient.*
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import Events.LedgerOpened
import org.litote.kmongo.*

private const val SUBSCRIBER_NAME = "ledger-read-projection"

private fun handleEvent(ledgers: MongoCollection<ReadLedger>, event : ResolvedEvent) {
    when (event.originalEvent.eventType) {
        "events.LedgerOpened" -> {
            println("match")
            val originalEvent = event.originalEvent.getEventDataAs(LedgerOpened::class.java)
            ledgers.insertOne(ReadLedger(originalEvent.uuid, originalEvent.accountUuid))
        }
        else -> null
    }
}

fun startLedgerReadProjection(esClient: EventStoreDBClient, mongoClient: MongoClient) {
    val database = mongoClient.getDatabase("test")
    val ledgers = database.getCollection<ReadLedger>()
    val allReadPositions = database.getCollection<AllReadPosition>()
    val recordPosition = allPositionRecorder(allReadPositions, SUBSCRIBER_NAME)

    val listener: SubscriptionListener = object : SubscriptionListener() {
        val mongoSession = mongoClient.startSession()

        override fun onEvent(subscription: Subscription?, event: ResolvedEvent) {
            mongoSession.use { clientSession ->
                handleEvent(ledgers, event)
                recordPosition(event)
            }
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