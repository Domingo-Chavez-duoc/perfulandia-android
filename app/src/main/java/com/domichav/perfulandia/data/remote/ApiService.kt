package com.domichav.perfulandia.data.remote

import com.domichav.perfulandia.data.remote.dto.*
import com.domichav.perfulandia.data.remote.dto.perfume.PerfumeDto
import com.domichav.perfulandia.data.remote.dto.user.UserDto
import com.domichav.perfulandia.data.remote.dto.user.UsersResponse
import com.domichav.perfulandia.data.remote.dto.pedido.CreatePedidoRequest
import com.domichav.perfulandia.data.remote.dto.pedido.PedidoResponseDto
import com.domichav.perfulandia.data.remote.dto.resena.CreateResenaRequest
import com.domichav.perfulandia.data.remote.dto.resena.ResenaDto
import com.domichav.perfulandia.data.remote.dto.resena.UpdateResenaDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    //----- RESEÑA ENDPOINTS -----//

    /*
    GET /resena
    Listar todas las reseñas*/
    @GET("resena")
    suspend fun getAllResenas(): ApiResponse<List<ResenaDto>>

    /*
    GET /resena/{id}
    Obtener una reseña por su ID*/
    @GET("resena/{id}")
    suspend fun getResenaById(@Path("id") resenaId: String): ApiResponse<ResenaDto>

    /*
    POST /resena
    Crear una nueva reseña*/
    @POST("resena")
    suspend fun createResena(@Body createRequest: CreateResenaRequest): ApiResponse<ResenaDto>

    /*
    PATCH /resena/{id}
    Actualizar una reseña existente*/
    @PATCH("resena/{id}")
    suspend fun updateResena(
        @Path("id") resenaId: String,
        @Body updateRequest: Map<String, @JvmSuppressWildcards Any> // <-- Punto a mejorar
    ): ApiResponse<ResenaDto>

    /*
    DELETE /resena/{id}
    Eliminar una reseña*/
    @DELETE("resena/{id}")
    suspend fun deleteResena(@Path("id") resenaId: String): ApiResponse<Unit>

    /*
    GET /resena/perfume/{perfumeId}
    Obtener todas las reseñas de un perfume específico*/
    @GET("resena/perfume/{perfumeId}")
    suspend fun getResenasByPerfume(@Path("perfumeId") perfumeId: String): ApiResponse<List<ResenaDto>>

    /*
    GET /resena/cliente/{clienteId}
    Obtener todas las reseñas de un cliente específico*/
    @GET("resena/cliente/{clienteId}")
    suspend fun getResenasByCliente(@Path("clienteId") clienteId: String): ApiResponse<List<ResenaDto>>

    /*
    POST /resena/{id}/upload-image
    Subir una imagen para una reseña*/
    @Multipart
    @POST("resena/{id}/upload-image")
    suspend fun uploadResenaImage(
        @Path("id") resenaId: String,
        @Part file: MultipartBody.Part
    ): ApiResponse<Any>

    //----- PEDIDO ENDPOINTS (CORREGIDOS Y DENTRO DE LA INTERFAZ) -----//

    /**
     * Crea un nuevo pedido.
     * POST /pedido
     */
    @POST("pedido")
    suspend fun createPedido(@Body request: CreatePedidoRequest): ApiResponse<PedidoResponseDto>

    /**
     * Sube una imagen para un pedido.
     * POST /pedido/{id}/upload-image
     */
    @Multipart
    @POST("pedido/{id}/upload-image")
    suspend fun uploadPedidoImage(
        @Path("id") id: String,
        @Part file: MultipartBody.Part
    ): ApiResponse<Any>

    /**
     * Obtiene todos los pedidos (generalmente para un admin).
     * GET /pedido
     */
    @GET("pedido")
    suspend fun getPedidos(): ApiResponse<List<PedidoResponseDto>>

    /**
     * Obtiene un pedido específico por su ID.
     * GET /pedido/{id}
     */
    @GET("pedido/{id}")
    suspend fun getPedidoById(@Path("id") id: String): ApiResponse<PedidoResponseDto>

    /**
     * Actualiza un pedido (ej: cambiar el estado).
     * PATCH /pedido/{id}
     */
    @PATCH("pedido/{id}")
    suspend fun updatePedido(
        @Path("id") pedidoId: String,
        @Body updateRequest: Map<String, @JvmSuppressWildcards Any>
    ): ApiResponse<PedidoResponseDto>

    /**
     * Elimina un pedido.
     * DELETE /pedido/{id}
     */
    @DELETE("pedido/{id}")
    suspend fun deletePedido(@Path("id") id: String): ApiResponse<Unit>
}



