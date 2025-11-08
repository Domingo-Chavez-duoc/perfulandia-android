package com.domichav.perfulandia.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para la respuesta de login
 * Datos que RECIBIMOS del servidor tras login exitoso
 */
data class LoginResponse(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("username")
    val username: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("firstName")
    val firstName: String? = null,

    @SerializedName("lastName")
    val lastName: String? = null,

    // Algunos servidores devuelven 'authToken', otros 'accessToken'. Hacer ambos anulables y preferir authToken cuando esté presente.
    @SerializedName("accessToken")
    val accessToken: String? = null,

    @SerializedName("authToken")
    val authToken: String? = null,

    @SerializedName("refreshToken")
    val refreshToken: String? = null  // Opcional - Para renovar el token
)