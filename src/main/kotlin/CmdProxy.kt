import AccountsDomain.Commands.DepositFunds
import AccountsDomain.Commands.WithdrawFunds
import AccountsDomain.buildCatchupSubscriber
import TransactionsDomain.Commands.CompleteTransaction
import TransactionsDomain.Commands.RequestTransaction
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

    buildCatchupSubscriber(appTree.esClient(), appTree.mongoClient(), "command-handler-group") { event ->
        val originalEvent = event.originalEvent

        try {
            when (originalEvent.eventType) {
                "OpenAccount" -> {
                    val cmd = originalEvent.getEventDataAs(AccountsDomain.Commands.OpenAccount::class.java)
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

                "DepositFunds" -> {
                    val cmd = originalEvent.getEventDataAs(DepositFunds::class.java)
                    appTree.accountsDomainCommandHandler().handle(cmd)
                }

                "WithdrawFunds" -> {
                    val cmd = originalEvent.getEventDataAs(WithdrawFunds::class.java)
                    appTree.accountsDomainCommandHandler().handle(cmd)
                }

                "RequestTransaction" -> {
                    val cmd = originalEvent.getEventDataAs(RequestTransaction::class.java)
                    appTree.transactionsDomainCommandHandler().handle(cmd)
                }

                "CompleteTransaction" -> {
                    val cmd = originalEvent.getEventDataAs(CompleteTransaction::class.java)
                    appTree.transactionsDomainCommandHandler().handle(cmd)
                }
            }
        } catch (e: Exception) {
            println("##########")
            println(e.message)
            println("##########")
            return@buildCatchupSubscriber
        }
    }
}