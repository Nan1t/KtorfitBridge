package org.ktorbridge.services

import com.test.proto.AuthService
import io.ktor.server.routing.RoutingCall
import kotlinx.coroutines.delay
import org.ktorbridge.api.AuthRequest
import org.ktorbridge.api.AuthResponse

class AuthServiceImpl : AuthService {

    override suspend fun signUp(call: RoutingCall, req: AuthRequest): AuthResponse {
        delay(1000)
        return AuthResponse("test")
    }

    override suspend fun signIn(call: RoutingCall, req: AuthRequest): AuthResponse {
        delay(1000)
        return AuthResponse("test")
    }
}