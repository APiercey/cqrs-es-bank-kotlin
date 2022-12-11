import AccountsDomain.AccountRepo
import AccountsDomain.Operations.BlockAccount
import AccountsDomain.Operations.CloseAccount
import AccountsDomain.Operations.OpenAccount
import AccountsDomain.Operations.UnblockAccount
import ReadDomain.ReadAccount
import com.eventstore.dbclient.EventStoreDBClient
import com.mongodb.client.MongoDatabase
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import org.litote.kmongo.json
import java.util.*

fun startWebServer(esClient: EventStoreDBClient, database: MongoDatabase) {
    val repo = AccountRepo(esClient)
    val openAccountOp = OpenAccount(esClient)
    val closeAccountOp = CloseAccount(esClient)
    val blockAccountOp = BlockAccount(esClient)
    val unblockAccountOp = UnblockAccount(esClient)

    embeddedServer(Netty, port = 8080) {
        routing {
            post("/open_account") {
                var uuid = UUID.randomUUID().toString()

                if(openAccountOp.execute(uuid, "CHECKING")) {
                    call.respondText("{\"uuid\": \"$uuid\"}")
                } else {
                    call.respondText("An error occured")
                }
            }
            post("/accounts/{uuid}/block") {
                if(blockAccountOp.execute(call.parameters["uuid"].toString())) {
                    call.respondText("OK")
                } else {
                    call.respondText("An error occured")
                }
            }
            post("/accounts/{uuid}/unblock") {
                if(unblockAccountOp.execute(call.parameters["uuid"].toString())) {
                    call.respondText("OK")
                } else {
                    call.respondText("An error occured")
                }
            }
            post("/accounts/{uuid}/close") {
                var account = repo.fetch(call.parameters["uuid"].toString())
                if(account == null) {
                    call.respondText("404 Account not found")
                    return@post
                }

                try {
                    closeAccountOp.execute(account)
                    call.respondText("OK")
                } catch(e: Exception) {
                    call.respondText(e.localizedMessage)
                }
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
        }
    }.start(wait = true)
}