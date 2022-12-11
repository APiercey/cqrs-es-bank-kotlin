import AccountsDomain.AccountRepo
import AccountsDomain.Operations.BlockAccount
import AccountsDomain.Operations.CloseAccount
import AccountsDomain.Operations.OpenAccount
import AccountsDomain.Operations.UnblockAccount
import com.eventstore.dbclient.*
import com.mongodb.client.MongoDatabase
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.litote.kmongo.*
import java.util.*
import ReadDomain.startAccountReadProjection
import ReadDomain.startLedgerReadProjection
import LedgersDomain.startLedgerAccountCreatedSubscriber
import ReadDomain.ReadAccount

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

fun main(args: Array<String>) {
    val mongoClient = KMongo.createClient("mongodb://127.0.0.1:27017") //get com.mongodb.MongoClient new instance
    val database = mongoClient.getDatabase("test") //normal java driver usage

    val settings: EventStoreDBClientSettings = EventStoreDBConnectionString.parse("esdb://localhost:2113?tls=false")
    val client: EventStoreDBClient = EventStoreDBClient.create(settings)

    var httpServer = Thread() {
        startWebServer(client, database)
    }

    startAccountReadProjection(client, mongoClient)
    startLedgerReadProjection(client, mongoClient)

    startLedgerAccountCreatedSubscriber(client, mongoClient)

    httpServer.start()
    httpServer.join()
}