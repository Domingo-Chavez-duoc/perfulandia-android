package com.domichav.perfulandia.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Objeto de transferencia de datos (DTO) para la solicitud de registro.
 *
 */
data class SignupRequest(
    val name: String,
    val email: String,
    val password: String
)

/**
 * Objeto de transferencia de datos para la respuesta del registro.
 * Algunos APIs devuelven diferentes nombres de campo para el token (por ejemplo, "authToken" o "accessToken").
 * Tenemos ambos como nulos y dejamos que el repositorio elija el valor no nulo.
 */
data class SignupResponse(
    @SerializedName("authToken") val authToken: String? = null,
    @SerializedName("accessToken") val accessToken: String? = null,
    @SerializedName("user_id") val userId: Int? = null
)

/**
 * Transferencia de objetos de datos (DTO) para la respuesta del perfil del usuario.
 *
 */
data class UserResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String
    // Otros campos como 'created_at', 'account_id', 'role' pueden agregarse aquí si es necesario.
)
