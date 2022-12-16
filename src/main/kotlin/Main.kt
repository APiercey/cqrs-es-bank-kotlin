import ReadDomain.startAccountReadProjection
import ReadDomain.startTransactionsReadProjection
import TransactionsDomain.startAccountTransferEventsHandler

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
    startTransactionsReadProjection(esClient, mongoClient)
    startAccountTransferEventsHandler(appTree)

    httpServer.start()
    httpServer.join()
}