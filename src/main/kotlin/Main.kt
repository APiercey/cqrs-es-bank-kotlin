import com.eventstore.dbclient.*
import com.mongodb.client.MongoDatabase
import events.AccountBlocked
import events.AccountClosed
import events.AccountCreated
import events.AccountUnblocked
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import operations.*
import org.litote.kmongo.*
import java.util.*
import writeDomain.*

data class ReadAccount(val uuid: String, val type: String, val blocked: Boolean, val open: Boolean)

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
                if(closeAccountOp.execute(call.parameters["uuid"].toString())) {
                    call.respondText("OK")
                } else {
                    call.respondText("An error occured")
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
        }
    }.start(wait = true)
}

fun startReadProjection(esClient: EventStoreDBClient, database: MongoDatabase) {
    val listener: SubscriptionListener = object : SubscriptionListener() {
        override fun onEvent(subscription: Subscription?, event: ResolvedEvent) {
            if(!event.originalEvent.streamId.startsWith("account-")) { return Unit }

            val col = database.getCollection<ReadAccount>()

            when (event.originalEvent.eventType) {
                "events.AccountCreated" -> {
                    var event = event.originalEvent.getEventDataAs(AccountCreated::class.java)
                    col.insertOne(ReadAccount(event.uuid, event.type, false, true))
                }
                "events.AccountBlocked" -> {
                    var event = event.originalEvent.getEventDataAs(AccountBlocked::class.java)
                    col.updateOne(ReadAccount::uuid eq event.uuid, set(ReadAccount::blocked setTo true))
                }
                "events.AccountUnblocked" -> {
                    var event = event.originalEvent.getEventDataAs(AccountUnblocked::class.java)
                    col.updateOne(ReadAccount::uuid eq event.uuid, set(ReadAccount::blocked setTo false))
                }
                "events.AccountClosed" -> {
                    var event = event.originalEvent.getEventDataAs(AccountClosed::class.java)
                    col.updateOne(ReadAccount::uuid eq event.uuid, set(ReadAccount::open setTo false))
                }
            }
        }
    }

    esClient.subscribeToAll(
        listener,
        SubscribeToAllOptions.get().fromStart()
    )
}

fun main(args: Array<String>) {
    val kMongoClient = KMongo.createClient("mongodb://127.0.0.1:27017") //get com.mongodb.MongoClient new instance
    val database = kMongoClient.getDatabase("test") //normal java driver usage

    val settings: EventStoreDBClientSettings = EventStoreDBConnectionString.parse("esdb://localhost:2113?tls=false")
    val client: EventStoreDBClient = EventStoreDBClient.create(settings)


    var httpServer = Thread() {
        startWebServer(client, database)
    }

    startReadProjection(client, database)

    httpServer.start()
    httpServer.join()
}