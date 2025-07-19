package org.ktorbridge.api

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val login: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val accessToken: String
)

interface AuthService {

    @POST("signUp")
    suspend fun signUp(@Body req: AuthRequest): AuthResponse

    @POST("signIn")
    suspend fun signIn(@Body req: AuthRequest): AuthResponse

}