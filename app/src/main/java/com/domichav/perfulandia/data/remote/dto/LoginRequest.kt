package com.domichav.perfulandia.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para la petición de login
 * Datos que ENVIAMOS al servidor
 */
data class LoginRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("expiresInMins")
    val expiresInMins: Int = 30  // El token expira en 30 min
)
