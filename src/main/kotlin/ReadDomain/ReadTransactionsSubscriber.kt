package ReadDomain

import Helpers.AllReadPosition
import Helpers.allPositionRecorder
import com.eventstore.dbclient.*
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import Events.TransactionRequested
import org.litote.kmongo.*

private const val SUBSCRIBER_NAME = "transactions-read-projection"

private fun handleEvent(transactions: MongoCollection<ReadTransaction>, event : ResolvedEvent) {
    when (event.originalEvent.eventType) {
        "events.TransactionRequested" -> {
            val originalEvent = event.originalEvent.getEventDataAs(TransactionRequested::class.java)
            transactions.insertOne(ReadTransaction(originalEvent.uuid, originalEvent.creditorUuid, originalEvent.debtorUuid, originalEvent.amount, "PENDING"))
        }
        else -> println(event.originalEvent.eventType)
    }
}

fun startTransactionsReadProjection(esClient: EventStoreDBClient, mongoClient: MongoClient) {
    val database = mongoClient.getDatabase("test")
    val transactions = database.getCollection<ReadTransaction>()
    val allReadPositions = database.getCollection<AllReadPosition>()
    val recordPosition = allPositionRecorder(allReadPositions, SUBSCRIBER_NAME)

    val listener: SubscriptionListener = object : SubscriptionListener() {
        val mongoSession = mongoClient.startSession()

        override fun onEvent(subscription: Subscription?, event: ResolvedEvent) {
            mongoSession.use { clientSession ->
                handleEvent(transactions, event)
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