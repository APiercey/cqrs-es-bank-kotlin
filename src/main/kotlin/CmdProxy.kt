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

                try {
                    when(originalEvent.eventType) {
                        "OpenAccount" -> {
                            val cmd = originalEvent.getEventDataAs(AccountsDomain.Commands.OpenAccount::class.java)
                            appTree.accountsDomainCommandHandler().handle(cmd)
                        }
                        "AssignLedger" -> {
                            val cmd = originalEvent.getEventDataAs(AccountsDomain.Commands.AssignLedger::class.java)
                            appTree.accountsDomainCommandHandler().handle(cmd)
                        }
                        "BlockAccount" -> {
                            val cmd = originalEvent.getEventDataAs(AccountsDomain.Commands.BlockAccount::class.java)
                            appTree.accountsDomainCommandHandler().handle(cmd)
                        }
                        "UnblockAccount" -> {
                            val cmd = originalEvent.getEventDataAs(AccountsDomain.Commands.UnblockAccount::class.java)
                            appTree.accountsDomainCommandHandler().handle(cmd)
                        }
                        "CloseAccount" -> {
                            val cmd = originalEvent.getEventDataAs(AccountsDomain.Commands.CloseAccount::class.java)
                            appTree.accountsDomainCommandHandler().handle(cmd)
                        }
                    }
                } catch(e : Exception) {
                    println("##########")
                    println(e.message)
                    println("##########")
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