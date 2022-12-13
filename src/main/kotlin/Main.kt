import AccountsDomain.startLedgerOpenedAssignLedgerSubscriber
import ReadDomain.startAccountReadProjection
import ReadDomain.startLedgerReadProjection
import ReadDomain.startTransactionsReadProjection
import LedgersDomain.startLedgerAccountCreatedSubscriber

fun main(args: Array<String>) {
    val appTree : AppTree = AppTree()
    val mongoClient = appTree.mongoClient()
    val esClient = appTree.esClient()

    var httpServer = Thread() {
        startWebServer(appTree)
    }

//    buildCommandGroup(appTree.esPersistentClient())
    startCommandProxy(appTree)

    startAccountReadProjection(esClient, mongoClient)
    startLedgerReadProjection(esClient, mongoClient)
    startTransactionsReadProjection(esClient, mongoClient)

    startLedgerOpenedAssignLedgerSubscriber(appTree)
    startLedgerAccountCreatedSubscriber(esClient, mongoClient)

    httpServer.start()
    httpServer.join()
}