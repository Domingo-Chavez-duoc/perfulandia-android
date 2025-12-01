package com.domichav.perfulandia.data.remote

import com.domichav.perfulandia.data.remote.dto.*
import com.domichav.perfulandia.data.remote.dto.perfume.PerfumeDto
import com.domichav.perfulandia.data.remote.dto.user.UserDto
import com.domichav.perfulandia.data.remote.dto.user.UsersResponse
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


//----- RESENA ENDPOINTS -----//

/**
 * Obtiene todas las reseñas.
 * GET /resena
 */
@GET("resena")
suspend fun getResenas(): List<ResenaDto>

/**
 * Crea una nueva reseña para un perfume.
 * POST /resena
 */
@POST("resena")
suspend fun createResena(@Body request: CreateResenaRequest): ResenaDto

/**
 * Obtiene una reseña específica por su ID.
 * GET /resena/{id}
 */
@GET("resena/{id}")
suspend fun getResenaById(@Path("id") id: String): ResenaDto

/**
 * Actualiza una reseña existente.
 * PUT /resena/{id}
 */
@PUT("resena/{id}")
suspend fun updateResena(@Path("id") id: String, @Body request: CreateResenaRequest): ResenaDto

/**
 * Elimina una reseña.
 * DELETE /resena/{id}
 */
@DELETE("resena/{id}")
suspend fun deleteResena(@Path("id") id: String): Unit // O un objeto de respuesta si la API lo devuelve



//----- PEDIDO ENDPOINTS -----//

/**
 * Obtiene todos los pedidos del usuario autenticado.
 * GET /pedido
 */
@GET("pedido")
suspend fun getPedidos(): List<PedidoResponseDto>

/**
 * Crea un nuevo pedido.
 * POST /pedido
 */
@POST("pedido")
suspend fun createPedido(@Body request: CreatePedidoRequest): PedidoResponseDto

/**
 * Obtiene un pedido específico por su ID.
 * GET /pedido/{id}
 */
@GET("pedido/{id}")
suspend fun getPedidoById(@Path("id") id: String): PedidoResponseDto

