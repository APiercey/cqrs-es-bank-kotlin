import AccountsDomain.Commands.*
import ReadDomain.ReadAccount
import ReadDomain.ReadTransaction
import TransactionsDomain.Commands.RequestTransaction
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.litote.kmongo.json
import java.util.*
import com.google.gson.Gson

fun startWebServer(appTree: AppTree) {
    val database = appTree.mongoDatabase()
    val bus = appTree.bus()
    embeddedServer(Netty, port = 8080) {
        val gson = Gson()

        routing {
            post("/open_account") {
                val uuid = UUID.randomUUID().toString()

                bus.send(OpenAccount(uuid, "CHECKING"))
                call.respondText("{\"uuid\": \"$uuid\"}")
            }
            post("/accounts/{uuid}/block") {
                bus.send(BlockAccount(call.parameters["uuid"].toString()))
                call.respondText("OK")
            }
            post("/accounts/{uuid}/unblock") {
                bus.send(UnblockAccount(call.parameters["uuid"].toString()))
                call.respondText("OK")
            }
            post("/accounts/{uuid}/close") {
                bus.send(CloseAccount(call.parameters["uuid"].toString()))
                call.respondText("OK")
            }
            post("/accounts/{uuid}/deposit") {
                val body = gson.fromJson(call.receiveText(), HashMap::class.java)
                val amount = (body["amount"] as? Double ?: 0).toInt()

                bus.send(DepositFunds(call.parameters["uuid"].toString(), amount))
                call.respondText("OK")
            }
            post("/accounts/{uuid}/withdraw") {
                val body = gson.fromJson(call.receiveText(), HashMap::class.java)
                val amount = (body["amount"] as? Double ?: 0).toInt()

                bus.send(WithdrawFunds(call.parameters["uuid"].toString(), amount))
                call.respondText("OK")
            }
            get("/accounts/{uuid}") {
                val account : ReadAccount ? = database
                    .getCollection<ReadAccount>()
                    .findOne(ReadAccount::uuid eq call.parameters["uuid"].toString())

                if(account != null) {
                    call.respondText(account.json)
                } else {
                    call.respondText("404")
                }
            }
            get("/accounts") {
                val accounts = database
                    .getCollection<ReadAccount>()
                    .find()
                    .toList()

                call.respondText(accounts.json)
            }
            get("/transactions") {
                val transactions = database
                    .getCollection<ReadTransaction>()
                    .find()
                    .toList()

                call.respondText(transactions.json)
            }
            get("/transactions/{uuid}") {
                val transaction : ReadTransaction ? = database
                    .getCollection<ReadTransaction>()
                    .findOne(ReadTransaction::uuid eq call.parameters["uuid"].toString())

                if(transaction != null) {
                    call.respondText(transaction.json)
                } else {
                    call.respondText("404")
                }
            }
            post("/transactions/account_transfer") {
                val body = gson.fromJson(call.receiveText(), HashMap::class.java)
                val transactionUuid = UUID.randomUUID().toString()

                val cmd = RequestTransaction(
                    transactionUuid,
                    body["sender_uuid"].toString(),
                    body["receiver_uuid"].toString(),
                    (body["amount"] as? Double ?: 0).toInt(),
                    transactionUuid
                )

                bus.send(cmd)

                call.respondText("{\"uuid\": \"$transactionUuid\"}")
            }
        }
    }.start(wait = true)
}