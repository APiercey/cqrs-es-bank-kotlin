package TransactionsDomain.AccountTransfer

import AccountsDomain.Commands.*
import Architecture.BaseEvent
import Architecture.Command
import Architecture.Saga
import Events.FundsDeposited
import Events.FundsWithdrawn
import Events.TransactionRequested
import TransactionsDomain.Commands.CompleteTransaction

class AccountTransferSaga() : Saga() {
    private var uncomittedEvents : List<BaseEvent> = mutableListOf()
    private var undispatchedCommands : List<Command> = mutableListOf()

    private var senderUuid : String = ""
    private var receiverUuid : String = ""
    private var amount : Int = 0

    override fun transition(event : BaseEvent) {
        when(event) {
            is TransactionRequested -> {
                uuid = event.uuid
                receiverUuid = event.receiverUuid
                senderUuid = event.senderUuid
                amount = event.amount

                send(WithdrawFunds(senderUuid, amount, uuid))
            }
            is FundsWithdrawn -> {
                send(DepositFunds(receiverUuid, amount, uuid))
            }
            is FundsDeposited -> {
                send(CompleteTransaction(uuid))
            }
        }

        uncomittedEvents = uncomittedEvents.plus(event)
    }

    override fun uncomittedEvents(): List<BaseEvent> {
        return uncomittedEvents
    }

    override fun clearUncomittedEvents() {
        uncomittedEvents = mutableListOf()
    }

    override fun undispatchedCommands(): List<Command> {
        return undispatchedCommands
    }

    override fun clearUndispatchedCommands() {
        undispatchedCommands = mutableListOf()
    }

    private fun send(cmd : Command) {
        undispatchedCommands = undispatchedCommands.plus(cmd)
    }
}