package LedgersDomain.Commands

import Architecture.Command

class OpenLedger(var ledgerUuid : String, var accountUuid : String) : Command() {
    override fun name(): String { return "OpenLedger" }
}