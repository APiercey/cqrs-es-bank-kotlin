//package LedgersDomain.Operations
//
//import com.eventstore.dbclient.EventStoreDBClient
//import Events.LedgerOpened
//import com.mongodb.client.MongoClient
//
//class DepositFunds(private var client: EventStoreDBClient, private var mongoclient : MongoClient) {
//    fun execute(accountUuid : String, amount : Int) : Boolean {
//
//        val event = LedgerOpened()
//        event.uuid = uuid
//        event.accountUuid = accountUuid
//
//        client.appendToStream("ledger-${event.uuid}", event.toEventData()).get()
//
//        return true
//    }
//}