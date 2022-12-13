import Events.AccountCreated
import com.eventstore.dbclient.*

private const val commandsStream : String = "commands"
private const val commandsGroup : String = "command-handler-group"

fun buildCommandGroup(esClient: EventStoreDBPersistentSubscriptionsClient) {
    esClient.createToStream(
        commandsStream,
        commandsGroup,
        CreatePersistentSubscriptionToStreamOptions
            .get()
            .fromStart());
}

fun startCommandProxy(appTree : AppTree) {
    val esClient = appTree.esPersistentClient()

    esClient.subscribeToStream(
        commandsStream,
        commandsGroup,
        object : PersistentSubscriptionListener() {
            override fun onEvent(subscription: PersistentSubscription, retryCount: Int, event: ResolvedEvent) {
                val originalEvent = event.originalEvent

                when(originalEvent.eventType) {
                    "OpenAccount" -> {
                        val cmd = originalEvent.getEventDataAs(AccountsDomain.Commands.OpenAccount::class.java)
                        appTree.accountsDomainCommandHandler().handle(cmd)
                    }
                    else -> null
                }
            }

            override fun onError(subscription: PersistentSubscription, throwable: Throwable) {
                println("Subscription was dropped due to " + throwable.message)
            }

//            override fun onCancelled(subscription: PersistentSubscription) {
//                println("Subscription is cancelled")
//            }
        })
}