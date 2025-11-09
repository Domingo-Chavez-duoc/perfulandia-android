package com.domichav.perfulandia.data.remote.api

import com.domichav.perfulandia.data.remote.dto.LoginRequest
import com.domichav.perfulandia.data.remote.dto.LoginResponse
import com.domichav.perfulandia.data.remote.dto.SignupRequest
import com.domichav.perfulandia.data.remote.dto.SignupResponse
import com.domichav.perfulandia.data.remote.dto.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Define la API de autenticación
 *
 */
interface AuthApiService {

    /**
     * Performa una petición de login
     */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    /**
     * Performa una petición de signup
     */
    @POST("auth/signup")
    suspend fun signup(@Body request: SignupRequest): SignupResponse

    /**
     * Hace un fetch del perfil del usuario. El token de autenticación se añade automáticamente por la AuthInterceptor
     */
    @GET("auth/me")
    suspend fun getMe(): UserResponse
}
