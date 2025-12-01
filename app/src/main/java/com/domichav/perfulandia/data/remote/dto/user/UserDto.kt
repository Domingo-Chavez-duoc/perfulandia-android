package com.domichav.perfulandia.data.remote.dto.user

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Representa la entidad 'User' del backend.
 * Se enfoca exclusivamente en la autenticación y la identidad básica.
 */
data class UserDto(
    @SerializedName("_id")
    val id: String,

    @SerializedName("email")
    val email: String,

    // El password NUNCA se envía al cliente

    @SerializedName("role")
    val role: String,

    @SerializedName("avatar")
    val avatar: String?, // Opcional

    @SerializedName("isActive")
    val isActive: Boolean,

    @SerializedName("emailVerified")
    val emailVerified: Boolean,

    // Timestamps que vienen del backend (aparentemente no se usan en la app)
    @SerializedName("createdAt")
    val createdAt: Date,

    @SerializedName("updatedAt")
    val updatedAt: Date
)