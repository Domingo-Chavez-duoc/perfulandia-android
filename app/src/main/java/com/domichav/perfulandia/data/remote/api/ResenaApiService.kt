package com.domichav.perfulandia.data.remote.api

import com.domichav.perfulandia.data.remote.dto.resena.CreateResenaRequest
import com.domichav.perfulandia.data.remote.dto.resena.ResenaDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ResenaApiService {

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
}