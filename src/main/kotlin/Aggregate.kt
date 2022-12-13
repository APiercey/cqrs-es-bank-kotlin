import Events.BaseEvent

abstract class Aggregate {
    private var _changes : List<BaseEvent> = mutableListOf()

    fun getMutations() : List<BaseEvent> {
        return _changes
    }

    fun clearMutations() {
        _changes = listOf()
    }

    fun enqueue(event : BaseEvent) {
        apply(event)
        _changes = _changes.plus(event)
    }

    abstract fun apply(event : BaseEvent)
}