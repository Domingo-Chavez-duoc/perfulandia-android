package com.domichav.perfulandia.data.remote.api

import com.domichav.perfulandia.data.remote.dto.pedido.CreatePedidoRequest
import com.domichav.perfulandia.data.remote.dto.pedido.PedidoResponseDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface PedidoApiService {

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