import io.ktor.server.application.*
import com.apurebase.kgraphql.GraphQL

fun Application.configureGraphQL() {
    install(GraphQL) {
        playground = true
        schema {
            // TODO: next section
        }
    }
}