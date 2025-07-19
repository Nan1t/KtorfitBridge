package org.ktorbridge.api

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.request.forms.MultiPartFormDataContent
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val name: String,
    val email: String
)

interface UserService {

    @GET("users")
    suspend fun list(): List<User>

    @GET("users/{name}")
    suspend fun find(@Path("name") name: String): User?

    @GET("users/find")
    suspend fun findQuery(@Query("id") id: Long, @Query("fast") fast: Boolean? = null): User

    @POST("add")
    suspend fun add(@Body user: User)

    @POST("avatar")
    suspend fun uploadAvatar(@Body mp: MultiPartFormDataContent)

}