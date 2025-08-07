package org.ktorbridge.services

import com.test.proto.UserService
import io.ktor.http.content.MultiPartData
import io.ktor.server.routing.RoutingCall
import kotlinx.coroutines.delay
import org.ktorbridge.api.User

class UserServiceImpl : UserService {

    override suspend fun list(call: RoutingCall): List<User> {
        return listOf(
            User(77, "test", "test@mail.com")
        )
    }

    override suspend fun find(call: RoutingCall, name: String): User {
        return User(77, name, "test@mail.com")
    }

    override suspend fun findQuery(call: RoutingCall, id: Long, fast: Boolean?): User {
        return User(id.toInt(), "test", "test@mail.com")
    }

    override suspend fun add(call: RoutingCall, user: User) {
        delay(3000)
    }

    override suspend fun uploadAvatar(call: RoutingCall, mp: MultiPartData) {
        delay(3000)
    }
}