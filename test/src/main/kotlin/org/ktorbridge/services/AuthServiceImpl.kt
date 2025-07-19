package org.ktorbridge.services

import kotlinx.coroutines.delay
import org.ktorbridge.api.AuthRequest
import org.ktorbridge.api.AuthResponse
import org.ktorbridge.api.server.AuthService

class AuthServiceImpl : AuthService {

    override suspend fun signUp(req: AuthRequest): AuthResponse {
        delay(1000)
        return AuthResponse("test")
    }

    override suspend fun signIn(req: AuthRequest): AuthResponse {
        delay(1000)
        return AuthResponse("test")
    }
}