package AccountsDomain

import Helpers.AllReadPosition
import Helpers.allPositionRecorder
import com.eventstore.dbclient.*
import com.mongodb.client.MongoClient
import org.litote.kmongo.*

fun buildCatchupSubscriber(
    esClient: EventStoreDBClient,
    mongoClient: MongoClient,
    subscriberName: String,
    handleEvent: (ResolvedEvent) -> Unit
) {
    val database = mongoClient.getDatabase("test")
    val allReadPositions = database.getCollection<AllReadPosition>()
    val recordPosition = allPositionRecorder(allReadPositions, subscriberName)

    val listener: SubscriptionListener = object : SubscriptionListener() {
        val mongoSession = mongoClient.startSession()

        override fun onEvent(subscription: Subscription?, event: ResolvedEvent) {
            // TODO: Transaction?
            mongoSession.use { clientSession ->
                try {
                    handleEvent(event)
                    recordPosition(event)
                } catch(e : Exception) {
                    println("Unhandled EventHandler Exception")
                    println(e.message)
                }

            }
        }
    }

    val position = allReadPositions.findOne(AllReadPosition::subscriberName eq subscriberName)

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