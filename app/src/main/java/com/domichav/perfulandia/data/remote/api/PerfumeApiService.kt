package com.domichav.perfulandia.data.remote.api

import com.domichav.perfulandia.data.remote.dto.perfume.CreatePerfumeRequest
import com.domichav.perfulandia.data.remote.dto.perfume.PerfumeDto
import com.domichav.perfulandia.data.remote.dto.perfume.PerfumePopulatedDto
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface PerfumeApiService {

    //----- PERFUME ENDPOINTS -----//
    @GET("perfume")
    suspend fun getPerfumes(): ApiResponse<List<PerfumeDto>>

    @GET("perfume/{id}")
    suspend fun getPerfumeById(@Path("id") perfumeId: String): ApiResponse<PerfumePopulatedDto>

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
        @Query("precioMin") precioMin: Double? = null,
        @Query("precioMax") precioMax: Double? = null
    ): ApiResponse<List<PerfumePopulatedDto>>

    @Multipart
    @POST("perfume/{id}/upload-image")
    suspend fun uploadPerfumeImage(
        @Path("id") perfumeId: String,
        @Part file: MultipartBody.Part
    ): ApiResponse<Any> // 'Any' porque la respuesta es compleja, se puede crear un DTO específico
}