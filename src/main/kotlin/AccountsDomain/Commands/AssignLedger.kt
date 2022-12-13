package AccountsDomain.Commands

import Commands.Command

class AssignLedger(var accountUuid : String = "", var ledgerUuid : String = "") : Command() {
    override fun name(): String { return "AssignLedger" }
}