package Server

import AppTree
import io.ktor.server.engine.*
import io.ktor.server.netty.*


fun startWebServer(appTree: AppTree) {
    embeddedServer(Netty, port = 8080) {
        configureGraphQL(appTree)
        configureRest(appTree)
    }.start(wait = true)
}