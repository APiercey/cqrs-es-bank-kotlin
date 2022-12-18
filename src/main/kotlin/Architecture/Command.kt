package Architecture

abstract class Command {
    abstract fun name() : String
    abstract fun correlationId() : String
}