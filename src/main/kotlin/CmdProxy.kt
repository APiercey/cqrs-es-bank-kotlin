import Architecture.DomainError
import Architecture.Command
import AccountsDomain.Commands.*
import TransactionsDomain.Commands.*
import AccountsDomain.buildPersistenceStreamSubscriber
import Architecture.CapturedDomainError
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

private fun buildCommand(event : ResolvedEvent) : Command? {
    val originalEvent = event.originalEvent

    return when (originalEvent.eventType) {
        "OpenAccount" -> originalEvent.getEventDataAs(OpenAccount::class.java)
        "BlockAccount" -> originalEvent.getEventDataAs(BlockAccount::class.java)
        "UnblockAccount" -> originalEvent.getEventDataAs(UnblockAccount::class.java)
        "CloseAccount" -> originalEvent.getEventDataAs(CloseAccount::class.java)
        "DepositFunds" -> originalEvent.getEventDataAs(DepositFunds::class.java)
        "WithdrawFunds" -> originalEvent.getEventDataAs(WithdrawFunds::class.java)
        "RequestTransaction" -> originalEvent.getEventDataAs(RequestTransaction::class.java)
        "CompleteTransaction" -> originalEvent.getEventDataAs(CompleteTransaction::class.java)
        "FailTransaction" -> originalEvent.getEventDataAs(FailTransaction::class.java)
        else -> null
    }
}

fun startCommandProxy(appTree : AppTree) {
    val accountsDomainCommandHandler = appTree.accountsDomainCommandHandler()
    val transactionsDomainCommandHandler = appTree.transactionsDomainCommandHandler()

    buildPersistenceStreamSubscriber(appTree.esPersistentClient(), commandsStream, commandsGroup) { event ->
        val cmd : Command = buildCommand(event) ?: return@buildPersistenceStreamSubscriber

        try {
            when (cmd) {
                is OpenAccount -> accountsDomainCommandHandler.handle(cmd)
                is BlockAccount -> accountsDomainCommandHandler.handle(cmd)
                is UnblockAccount -> accountsDomainCommandHandler.handle(cmd)
                is CloseAccount -> accountsDomainCommandHandler.handle(cmd)
                is DepositFunds -> accountsDomainCommandHandler.handle(cmd)
                is WithdrawFunds -> accountsDomainCommandHandler.handle(cmd)
                is RequestTransaction -> transactionsDomainCommandHandler.handle(cmd)
                is CompleteTransaction -> transactionsDomainCommandHandler.handle(cmd)
                is FailTransaction -> transactionsDomainCommandHandler.handle(cmd)
            }
        } catch (e: DomainError) {
            appTree.bus.sendError(CapturedDomainError(e.localizedMessage, cmd.correlationId(), cmd.name()))
        }
    }
}