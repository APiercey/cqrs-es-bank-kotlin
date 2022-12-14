//package TransactionsDomain
//
//import Architecture.Aggregate
//import Architecture.BaseEvent
//import Events.*
//import TransactionsDomain.Commands.*
//
//class Transaction() : Aggregate() {
//    var uuid : String = ""
//    var creditorUuid : String = ""
//    var debtorUuid : String = ""
//    var amount : Int = 0
//
//    constructor(uuid: String = "", creditorUuid: String = "", debtorUuid: String = "", amount: Int = 0) : this() {
//        enqueue(TransactionRequested(uuid, creditorUuid, debtorUuid, amount))
//    }
//
//    override fun apply(event: BaseEvent) {
//        when(event) {
//            is LedgerOpened -> applyEvent(event)
//            else -> null
//        }
//    }
//
//    private fun applyEvent(event: LedgerOpened) {
//        uuid = event.uuid
//        accountUuid = event.accountUuid
//        balance = 0
//    }
//}