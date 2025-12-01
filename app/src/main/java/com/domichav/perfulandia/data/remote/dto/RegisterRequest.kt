package com.domichav.perfulandia.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para la petición de registro
 * Datos que ENVIAMOS al servidor para crear un nuevo usuario
 */
data class RegisterRequest(
    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)
