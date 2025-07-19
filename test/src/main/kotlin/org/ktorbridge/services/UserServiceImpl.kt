package org.ktorbridge.services

import io.ktor.http.content.MultiPartData
import kotlinx.coroutines.delay
import org.ktorbridge.api.User
import org.ktorbridge.api.server.UserService

class UserServiceImpl : UserService {

    override suspend fun list(): List<User> {
        return listOf(
            User(77, "test", "test@mail.com")
        )
    }

    override suspend fun find(name: String): User {
        return User(77, name, "test@mail.com")
    }

    override suspend fun findQuery(value: Long): User {
        return User(value.toInt(), "test", "test@mail.com")
    }

    override suspend fun add(user: User) {
        delay(3000)
    }

    override suspend fun uploadAvatar(mp: MultiPartData) {
        delay(3000)
    }
}