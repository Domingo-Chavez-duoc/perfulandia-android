package com.domichav.perfulandia.data.remote

import com.domichav.perfulandia.data.remote.dto.*
import com.domichav.perfulandia.data.remote.dto.categoria.CategoriaDto
import com.domichav.perfulandia.data.remote.dto.categoria.CreateCategoriaRequest
import com.domichav.perfulandia.data.remote.dto.cliente.ClienteProfileDto
import com.domichav.perfulandia.data.remote.dto.perfume.PerfumeDto
import com.domichav.perfulandia.data.remote.dto.user.UserDto
import com.domichav.perfulandia.data.remote.dto.user.UsersResponse
import com.domichav.perfulandia.data.remote.dto.pedido.CreatePedidoRequest
import com.domichav.perfulandia.data.remote.dto.pedido.PedidoResponseDto
import com.domichav.perfulandia.data.remote.dto.perfume.CreatePerfumeRequest
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

    //----- CATEGORIA ENDPOINTS -----//

    /**
     * GET /categoria
     * Listar todas las categorías
     */
    @GET("categoria")
    suspend fun getAllCategorias(): ApiResponse<List<CategoriaDto>>

    /**
     * GET /categoria/{id}
     * Obtener una categoría por su ID
     */
    @GET("categoria/{id}")
    suspend fun getCategoriaById(@Path("id") categoriaId: String): ApiResponse<CategoriaDto>

    /**
     * POST /categoria
     * Crear una nueva categoría
     */
    @POST("categoria")
    suspend fun createCategoria(@Body createRequest: CreateCategoriaRequest): ApiResponse<CategoriaDto>

    /**
     * PATCH /categoria/{id}
     * Actualizar una categoría existente
     */
    @PATCH("categoria/{id}")
    suspend fun updateCategoria(
        @Path("id") categoriaId: String,
        @Body updateRequest: Map<String, @JvmSuppressWildcards Any>
    ): ApiResponse<CategoriaDto>

    /**
     * DELETE /categoria/{id}
     * Eliminar una categoría
     */
    @DELETE("categoria/{id}")
    suspend fun deleteCategoria(@Path("id") categoriaId: String): ApiResponse<Unit>

    /**
     * POST /categoria/{id}/upload-image
     * Subir una imagen para una categoría
     */
    @Multipart
    @POST("categoria/{id}/upload-image")
    suspend fun uploadCategoriaImage(
        @Path("id") categoriaId: String,
        @Part file: MultipartBody.Part
    ): ApiResponse<Any>

    //----- PERFUME ENDPOINTS -----//
    @GET("perfume")
    suspend fun getPerfumes(): ApiResponse<List<PerfumeDto>>

    @GET("perfume/{id}")
    suspend fun getPerfumeById(@Path("id") perfumeId: String): ApiResponse<PerfumeDto>

    @POST("perfume")
    suspend fun createPerfume(@Body createRequest: CreatePerfumeRequest): ApiResponse<PerfumeDto>

    @PATCH("perfume/{id}")
    suspend fun updatePerfume(
        @Path("id") perfumeId: String,
        @Body updateRequest: Map<String, @JvmSuppressWildcards Any> // Usar Map para actualizaciones parciales
    ): ApiResponse<PerfumeDto>

    @DELETE("perfume/{id}")
    suspend fun deletePerfume(@Path("id") perfumeId: String): ApiResponse<Unit> // Unit si la 'data' es nula o no importa

    @GET("perfume/categoria/{categoriaId}")
    suspend fun getPerfumesByCategoria(@Path("categoriaId") categoriaId: String): ApiResponse<List<PerfumeDto>>

    @GET("perfume/filtros")
    suspend fun filterPerfumes(
        @Query("genero") genero: String? = null,
        @Query("fragancia") fragancia: String? = null,
        @Query("tamaño") tamaño: String? = null,
        @Query("precioMin") precioMin: Double? = null,
        @Query("precioMax") precioMax: Double? = null
    ): ApiResponse<List<PerfumeDto>>

    @Multipart
    @POST("perfume/{id}/upload-image")
    suspend fun uploadPerfumeImage(
        @Path("id") perfumeId: String,
        @Part file: MultipartBody.Part
    ): ApiResponse<Any> // 'Any' porque la respuesta es compleja, se puede crear un DTO específico

}



