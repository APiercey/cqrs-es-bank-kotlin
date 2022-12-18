import AccountsDomain.AccountRepo
import Implementations.ESAccountRepo
import Implementations.ESTransactionRepo
import Implementations.ESAccountTransferSagaRepo
import Architecture.Bus
import TransactionsDomain.AccountTransfer.AccountTransferSagaRepo
import TransactionsDomain.TransactionRepo
import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBClientSettings
import com.eventstore.dbclient.EventStoreDBConnectionString
import com.eventstore.dbclient.EventStoreDBPersistentSubscriptionsClient
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import org.litote.kmongo.KMongo

class AppTree {
    val esStoreSettings : EventStoreDBClientSettings = EventStoreDBConnectionString.parse("esdb://localhost:2113?tls=false")
    fun esStoreSettings() : EventStoreDBClientSettings { return esStoreSettings }

    val mongoClient : MongoClient = KMongo.createClient("mongodb://127.0.0.1:27017")
    fun mongoClient() : MongoClient { return mongoClient }

    val mongoDatabase : MongoDatabase = mongoClient().getDatabase("test")
    fun mongoDatabase(): MongoDatabase { return mongoDatabase }

    val esClient : EventStoreDBClient = EventStoreDBClient.create(esStoreSettings())
    fun esClient() : EventStoreDBClient { return esClient }

    val esPersistentClient : EventStoreDBPersistentSubscriptionsClient = EventStoreDBPersistentSubscriptionsClient.create(esStoreSettings())
    fun esPersistentClient() : EventStoreDBPersistentSubscriptionsClient { return esPersistentClient }

    val accountRepo : AccountRepo = ESAccountRepo(esClient())
    fun accountRepo() : AccountRepo { return accountRepo }

    val transactionRepo : TransactionRepo = ESTransactionRepo(esClient())
    fun transactionRepo() : TransactionRepo { return transactionRepo }

    val accountsDomainCommandHandler : AccountsDomain.CommandHandler = AccountsDomain.CommandHandler(accountRepo())
    fun accountsDomainCommandHandler() : AccountsDomain.CommandHandler { return accountsDomainCommandHandler }

    val transactionsDomainCommandHandler : TransactionsDomain.CommandHandler = TransactionsDomain.CommandHandler(transactionRepo())
    fun transactionsDomainCommandHandler() : TransactionsDomain.CommandHandler { return transactionsDomainCommandHandler }

    val bus : Bus = Bus(esClient())
    fun bus() : Bus { return bus }

    val accountTransferSagaRepo : AccountTransferSagaRepo = ESAccountTransferSagaRepo(esClient(), bus)
    fun accountTransferSagaRepo() : AccountTransferSagaRepo { return accountTransferSagaRepo }
}