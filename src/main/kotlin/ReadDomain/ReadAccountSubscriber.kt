package ReadDomain

import Helpers.AllReadPosition
import Helpers.allPositionRecorder
import com.eventstore.dbclient.*
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoCollection
import Events.AccountBlocked
import Events.AccountClosed
import Events.AccountCreated
import Events.AccountUnblocked
import org.litote.kmongo.*

private const val SUBSCRIBER_NAME = "account-read-projection"

private fun handleEvent(accounts: MongoCollection<ReadAccount>, event : ResolvedEvent) {
    when (event.originalEvent.eventType) {
        "events.AccountCreated" -> {
            val originalEvent = event.originalEvent.getEventDataAs(AccountCreated::class.java)
            accounts.insertOne(ReadAccount(originalEvent.uuid, originalEvent.type, blocked = false, open = true, 0))
        }
        "events.AccountBlocked" -> {
            val originalEvent = event.originalEvent.getEventDataAs(AccountBlocked::class.java)
            accounts.updateOne(ReadAccount::uuid eq originalEvent.uuid, set(ReadAccount::blocked setTo true))
        }
        "events.AccountUnblocked" -> {
            val originalEvent = event.originalEvent.getEventDataAs(AccountUnblocked::class.java)
            accounts.updateOne(ReadAccount::uuid eq originalEvent.uuid, set(ReadAccount::blocked setTo false))
        }
        "events.AccountClosed" -> {
            val originalEvent = event.originalEvent.getEventDataAs(AccountClosed::class.java)
            accounts.updateOne(ReadAccount::uuid eq originalEvent.uuid, set(ReadAccount::open setTo false))
        }
        else -> null
    }
}

fun startAccountReadProjection(esClient: EventStoreDBClient, mongoClient: MongoClient) {
    val database = mongoClient.getDatabase("test")
    val accounts = database.getCollection<ReadAccount>()
    val allReadPositions = database.getCollection<AllReadPosition>()

    val recordPosition = allPositionRecorder(allReadPositions, SUBSCRIBER_NAME)

    val listener: SubscriptionListener = object : SubscriptionListener() {
        val mongoSession = mongoClient.startSession()

        override fun onEvent(subscription: Subscription?, event: ResolvedEvent) {
            mongoSession.use { clientSession ->
                handleEvent(accounts, event)
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