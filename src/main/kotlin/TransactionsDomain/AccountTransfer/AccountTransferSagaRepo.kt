package TransactionsDomain.AccountTransfer

import Architecture.Bus
import Events.*
import com.eventstore.dbclient.*

interface AccountTransferSagaRepo {
    fun fetch(uuid: String): AccountTransferSaga?
    fun save(saga : AccountTransferSaga)
}
