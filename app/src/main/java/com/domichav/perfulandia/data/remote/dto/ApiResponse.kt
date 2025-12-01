package com.domichav.perfulandia.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Clase genérica que modela la estructura de respuesta estándar del backend.
 * Permite a Retrofit/Gson convertir respuestas como:
 * { "success": true, "message": "...", "data": ..., "total": 10 }
 * en un objeto Kotlin.
 *
 * @param T El tipo de dato contenido en el campo 'data'. Puede ser cualquier objeto (Ej: PerfumeDto) o una lista (Ej: List<PerfumeDto>).
 */
data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String?,

    @SerializedName("data")
    val data: T?,

    @SerializedName("total")
    val total: Int?
)