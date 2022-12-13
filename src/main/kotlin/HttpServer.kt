import AccountsDomain.Commands.BlockAccount
import AccountsDomain.Commands.CloseAccount
import AccountsDomain.Commands.OpenAccount
import AccountsDomain.Commands.UnblockAccount
import TransactionsDomain.Operations.RequestTransaction

import ReadDomain.ReadAccount
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
    val esClient = appTree.esClient()
    val database = appTree.mongoDatabase()

    val requestTransactionOp = RequestTransaction(esClient)
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
            post("/accounts/{debtor_uuid}/transactions") {
                val body = gson.fromJson(call.receiveText(), HashMap::class.java)
                val transactionUuid = UUID.randomUUID().toString()

                val result = requestTransactionOp.execute(
                    transactionUuid,
                    body["creditor_uuid"].toString(),
                    call.parameters["debtor_uuid"].toString(),
                    body["amount"] as? Int ?: 0
                )

                if(result) {
                    call.respondText("{\"uuid\": \"$transactionUuid\"}")
                } else {
                    call.respondText("An error occured")
                }
            }
        }
    }.start(wait = true)
}