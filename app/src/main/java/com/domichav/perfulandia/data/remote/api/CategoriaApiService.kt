package com.domichav.perfulandia.data.remote.api

import com.domichav.perfulandia.data.remote.dto.categoria.CategoriaDto
import com.domichav.perfulandia.data.remote.dto.categoria.CreateCategoriaRequest
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface CategoriaApiService {

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
}