package Architecture

abstract class Saga {
    var uuid : String = ""

    abstract fun transition(event : BaseEvent)

    abstract fun uncomittedEvents() : List<BaseEvent>

    abstract fun clearUncomittedEvents()

    abstract fun undispatchedCommands() : List<Command>

    abstract fun clearUndispatchedCommands()
}