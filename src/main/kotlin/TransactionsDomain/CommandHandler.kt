package TransactionsDomain

import TransactionsDomain.Commands.CompleteTransaction
import TransactionsDomain.Commands.RequestTransaction

class CommandHandler(private val transactionRepo : TransactionRepo) {
    fun handle(cmd: RequestTransaction) {
        val transaction = Transaction(cmd.transactionUuid, cmd.senderUuid, cmd.receiverUuid, cmd.amount)
        transactionRepo.save(transaction)
    }
    fun handle(cmd: CompleteTransaction) {
        val transaction = transactionRepo.fetch(cmd.transactionUuid) ?: throw Exception("Transaction not found")
        transaction.handle(cmd)
        transactionRepo.save(transaction)
    }
}