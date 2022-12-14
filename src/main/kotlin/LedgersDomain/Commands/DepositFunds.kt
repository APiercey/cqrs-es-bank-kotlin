package LedgersDomain.Commands

import Architecture.Command

class DepositFunds(var ledgerUuid : String, var amount : Int) : Command() {
    override fun name(): String { return "DepositFunds" }
}