package Architecture

interface Saga {
    var uuid : String

    fun transition(event : BaseEvent)

    fun uncomittedEvents() : List<BaseEvent>

    fun clearUncomittedEvents()

    fun undispatchedCommands() : List<Command>

    fun clearUndispatchedCommands()
}