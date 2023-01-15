package Server

import AccountsDomain.Commands.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ReadDomain.ReadTransaction
import ReadDomain.ReadAccount
import com.apurebase.kgraphql.KGraphQL
import AppTree
import TransactionsDomain.Commands.RequestTransaction
import com.google.gson.Gson
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.getCollection
import java.util.*

data class CreateEntityResult(val accepted: Boolean, val uuid: String)
data class MutationResult(val accepted: Boolean)

fun Application.configureGraphQL(appTree: AppTree) {
    val database = appTree.mongoDatabase
    val bus = appTree.bus()
    val gson = Gson()

    val schema = KGraphQL.schema {
        query("GetAccount") {
            resolver { uuid: String ->
                database.getCollection<ReadAccount>().findOne(ReadAccount::uuid eq uuid)
            }
        }
        query("GetAccounts") {
            resolver { ->
                database.getCollection<ReadAccount>().find().toList()
            }
        }
        query("GetTransaction") {
            resolver { uuid: String ->
                database.getCollection<ReadTransaction>().findOne(ReadTransaction::uuid eq uuid)
            }
        }
        query("GetTransactions") {
            resolver {  ->
                database.getCollection<ReadAccount>().find().toList()
            }
        }
        mutation("OpenAccount") {
            resolver { ->
                val uuid = UUID.randomUUID().toString()
                bus.send(OpenAccount(uuid, "CHECKING"))
                CreateEntityResult(true, uuid)
            }
        }
        mutation("BlockAccount") {
            resolver { uuid : String ->
                bus.send(BlockAccount(uuid))
                MutationResult(true)
            }
        }
        mutation("UnblockAccount") {
            resolver { uuid : String ->
                bus.send(UnblockAccount(uuid))
                MutationResult(true)
            }
        }
        mutation("CloseAccount") {
            resolver { uuid : String ->
                bus.send(CloseAccount(uuid))
                MutationResult(true)
            }
        }
        mutation("DepositFunds") {
            resolver { uuid : String, amount : Int ->
                bus.send(DepositFunds(uuid, amount))
                MutationResult(true)
            }
        }
        mutation("WithdrawFunds") {
            resolver { uuid : String, amount : Int ->
                bus.send(WithdrawFunds(uuid, amount))
                MutationResult(true)
            }
        }
        mutation("RequestAccountTransfer") {
            resolver { senderUuid : String, receiverUuid : String, amount : Int ->
                val transactionUuid = UUID.randomUUID().toString()

                bus.send(RequestTransaction(
                    transactionUuid,
                    senderUuid,
                    receiverUuid,
                    amount,
                    transactionUuid
                ))

                CreateEntityResult(true, transactionUuid)
            }
        }
        type<ReadTransaction> {}
        type<ReadAccount> {}
        type<CreateEntityResult> {}
        type<MutationResult> {}
    }

    routing {
        post("/api-graphql") {
            val body = gson.fromJson(call.receiveText(), HashMap::class.java)
            val result = schema.execute(body["query"].toString(), body["variables"].toString())

            call.respond(result)
        }
    }
}
