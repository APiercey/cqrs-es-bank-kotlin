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