package com.domichav.perfulandia.data.remote.dto.cliente

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Representa la entidad 'ClienteProfile' del backend.
 * Contiene todos los datos de negocio asociados a un usuario ('User').
 */

data class ClienteProfileDto(
    @SerializedName("_id")
    val id: String,

    @SerializedName("user")
    val user: String,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("telefono")
    val telefono: String?,

    @SerializedName("direccion")
    val direccion: String?,

    @SerializedName("preferencias")
    val preferencias: List<String>?,

    @SerializedName("isActive")
    val isActive: Boolean,

    @SerializedName("createdAt")
    val createdAt: Date,

    @SerializedName("updatedAt")
    val updatedAt: Date
)