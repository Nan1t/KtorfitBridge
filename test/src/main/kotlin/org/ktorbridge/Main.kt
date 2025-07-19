package org.ktorbridge

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import org.ktorbridge.api.server.routeAuthService
import org.ktorbridge.api.server.routeUserService
import org.ktorbridge.services.AuthServiceImpl
import org.ktorbridge.services.UserServiceImpl

fun main() {
    val auth = AuthServiceImpl()
    val user = UserServiceImpl()

    embeddedServer(
        Netty,
        host = "0.0.0.0",
        port = 8080
    ) {
        install(ContentNegotiation) {
            json()
        }

        routing {
            routeAuthService(auth)
            routeUserService(user)
        }
    }.start(wait = true)
}