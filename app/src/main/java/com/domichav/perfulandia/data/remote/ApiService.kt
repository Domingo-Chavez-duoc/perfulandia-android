package com.domichav.perfulandia.data.remote

import com.domichav.perfulandia.data.remote.dto.*
import retrofit2.http.*

/**
 * Define los endpoints de tu API
 * Usando dummyjson como ejemplo de api rest con autenticación jwt
 */
interface ApiService {

    /**
     * LOGIN - autenticación de usuario
     * POST /user/login
     *
     * Ejemplo de uso:
     * val response = apiService.login(LoginRequest("emilys", "emilyspass"))
     * sessionManager.saveAuthToken(response.accessToken)
     */
    @POST("user/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    /**
     * REGISTRO - Crear un nuevo usuario
     * POST /users/add
     */
    @POST("users/add")
    suspend fun register(@Body request: RegisterRequest): UserDto

    /**
     * OBTENER USUARIO ACTUAL - necesita autenticación
     * GET /user/me
     *
     * Ojo: Este endpoint REQUIERE el token jwt
     * El AuthInterceptor se añade automáticamente
     *
     * Ejemplo de uso:
     * val currentUser = apiService.getCurrentUser()
     */
    @GET("user/me")
    suspend fun getCurrentUser(): UserDto

    /**
     * OBTENER LISTA DE USUARIOS
     * GET /users
     *
     * Ejemplo de uso:
     * val response = apiService.getUsers()
     * val usersList = response.users  - lista de UserDto
     */
    @GET("users")
    suspend fun getUsers(): UsersResponse

    /**
     *Busca usuario x nombre
     * GET /users/search?q={query}
     *
     * Ejemplo de uso:
     * val results = apiService.searchUsers("John")
     */
    @GET("users/search")
    suspend fun searchUsers(@Query("q") query: String): UsersResponse

    /**
     * OBTENER USUARIO x ID
     * GET /users/{id}
     *
     * Ejemplo de uso:
     * val user = apiService.getUserById(1)
     */
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): UserDto
}