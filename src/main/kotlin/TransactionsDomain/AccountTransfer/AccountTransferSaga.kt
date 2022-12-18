package TransactionsDomain.AccountTransfer

import AccountsDomain.Commands.*
import Architecture.BaseEvent
import Architecture.CapturedDomainError
import Architecture.Command
import Architecture.Saga
import Events.*
import TransactionsDomain.Commands.CompleteTransaction
import TransactionsDomain.Commands.FailTransaction

class AccountTransferSaga() : Saga {
    override var uuid: String = ""

    private var uncomittedEvents : List<BaseEvent> = mutableListOf()
    private var undispatchedCommands : List<Command> = mutableListOf()

    private var senderUuid : String = ""
    private var receiverUuid : String = ""
    private var amount : Int = 0
    private var sagaTerminal : Boolean = false

    override fun transition(event : BaseEvent) {
        if(sagaTerminal) { return@transition }

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
            is TransactionCompleted -> {
                sagaTerminal = true
            }
            is CapturedDomainError -> {
                if(event.originalCommandName == "WithdrawFunds") {
                    send(FailTransaction(uuid, uuid))
                }
                if(event.originalCommandName == "DepositFunds") {
                    send(FailTransaction(uuid, uuid))
                    // Return funds
                    send(DepositFunds(senderUuid, amount, uuid))
                }
            }
            is TransactionFailed -> {
                sagaTerminal = true
            }
        }

        record(event)
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

    private fun record(event : BaseEvent) {
        uncomittedEvents = uncomittedEvents.plus(event)
    }
}