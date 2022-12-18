package AccountsDomain

import com.eventstore.dbclient.*

fun buildPersistenceStreamSubscriber(
    esClient: EventStoreDBPersistentSubscriptionsClient,
    streamName : String,
    subscriberName: String,
    handleEvent: (ResolvedEvent) -> Unit
) {
    val listener: PersistentSubscriptionListener = object : PersistentSubscriptionListener() {
        override fun onEvent(subscription: PersistentSubscription?, retryCount : Int, event: ResolvedEvent) {
            if (subscription == null) { throw Exception("PersistenceSubscription has gone away.") }

            try {
                handleEvent(event)
                subscription.ack(event)
            } catch(e : Exception) {
                if(retryCount > 2) {
                    println("Message failed too often. Has been parked. ${event.event.eventId}")
                    subscription.nack(NackAction.Park, e.message, event)
                }

            }
        }
    }

    esClient.subscribeToStream(
        streamName,
        subscriberName,
        listener
    )
}