import AccountsDomain.AccountRepo
import Commands.Bus
import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBClientSettings
import com.eventstore.dbclient.EventStoreDBConnectionString
import com.eventstore.dbclient.EventStoreDBPersistentSubscriptionsClient
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.KMongo

class AppTree {
    fun esStoreSettings() : EventStoreDBClientSettings {
        return EventStoreDBConnectionString.parse("esdb://localhost:2113?tls=false")
    }

    fun mongoClient() : MongoClient {
        return KMongo.createClient("mongodb://127.0.0.1:27017")
    }

    fun mongoDatabase(): MongoDatabase {
        return mongoClient().getDatabase("test")
    }

    fun esClient() : EventStoreDBClient {
        return EventStoreDBClient.create(esStoreSettings())
    }

    fun esPersistentClient() : EventStoreDBPersistentSubscriptionsClient {
        return EventStoreDBPersistentSubscriptionsClient.create(esStoreSettings())
    }

    fun accountRepo() : AccountRepo {
        return AccountRepo(esClient())
    }

    fun accountsDomainCommandHandler() : AccountsDomain.CommandHandler {
        return AccountsDomain.CommandHandler(accountRepo())
    }

    fun bus() : Bus {
        return Bus(esClient())
    }
}