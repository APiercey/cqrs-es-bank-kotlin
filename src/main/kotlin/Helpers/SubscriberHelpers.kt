package Helpers

import com.eventstore.dbclient.ResolvedEvent
import com.eventstore.dbclient.StreamPosition
import com.mongodb.client.MongoCollection
import org.litote.kmongo.eq
import org.litote.kmongo.updateOne
import org.litote.kmongo.upsert

data class AllReadPosition(val subscriberName: String, val commitPosition: Long, val preparePosition: Long)

data class StreamReadPosition(val subscriberName: String, val revision: Long)

fun allPositionRecorder(allReadPositions : MongoCollection<AllReadPosition>, subscriberName: String) : (event: ResolvedEvent) -> Unit {
    return { event ->
        val position = StreamPosition
            .position(event.originalEvent.position)
            .position
            .get()

        val newPosition = AllReadPosition(subscriberName, position.commitUnsigned, position.prepareUnsigned)

        allReadPositions.updateOne(AllReadPosition::subscriberName eq subscriberName, newPosition, upsert())
    }
}

fun streamPositionRecorder(streamPositions : MongoCollection<StreamReadPosition>, subscriberName: String) : (event: ResolvedEvent) -> Unit {
    return { event ->
        val newPosition = StreamReadPosition(subscriberName, event.originalEvent.revision)

        streamPositions.updateOne(StreamReadPosition::subscriberName eq subscriberName, newPosition, upsert())
    }
}
