import Events.BaseEvent

open class Aggregate {
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

    open fun apply(event : BaseEvent) {

    }
}