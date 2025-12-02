package com.domichav.perfulandia.data.remote.api

import com.domichav.perfulandia.data.remote.dto.LoginRequest
import com.domichav.perfulandia.data.remote.dto.LoginResponse
import com.domichav.perfulandia.data.remote.dto.RegisterRequest
import com.domichav.perfulandia.data.remote.dto.user.UserDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UserApiService {

    //----- USUARIO Y CLIENTE ENDPOINTS -----//
    /**
     * Inicia sesión de usuario.
     * Corresponde a: POST /auth/login
     */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    /**
     * Registra un nuevo usuario de tipo CLIENTE.
     * Corresponde a: POST /auth/register
     */
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<UserDto>

    /**
     * Obtiene el perfil del usuario actualmente autenticado (User + ClienteProfile).
     * Corresponde a: GET /auth/profile
     */
    @GET("auth/profile")
    suspend fun getMyProfile(): ApiResponse<UserDto>

    /**
     * Sube una imagen para el perfil de un Cliente específico.
     * Corresponde a: POST /cliente/{id}/upload-image*/
    @Multipart
    @POST("auth/avatar")
    suspend fun uploadUserAvatar(@Part file: MultipartBody.Part): ApiResponse<UserDto>
}