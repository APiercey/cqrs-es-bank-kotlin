package LedgersDomain.Commands

import Architecture.Command

class WithdrawFunds(var ledgerUuid : String, var amount : Int) : Command() {
    override fun name(): String { return "WithdrawFunds" }
}