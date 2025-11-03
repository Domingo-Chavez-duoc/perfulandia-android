package com.domichav.perfulandia.data.remote.api

import com.domichav.perfulandia.data.remote.dto.SignupRequest
import com.domichav.perfulandia.data.remote.dto.SignupResponse
import com.domichav.perfulandia.data.remote.dto.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Defines the authentication endpoints for the API using Retrofit.
 */
interface AuthApiService {

    /**
     * Performs a signup request.
     */
    @POST("auth/signup")
    suspend fun signup(@Body request: SignupRequest): SignupResponse

    /**
     * Fetches the user profile. The auth token is added automatically by the AuthInterceptor.
     */
    @GET("auth/me")
    suspend fun getMe(): UserResponse
}
