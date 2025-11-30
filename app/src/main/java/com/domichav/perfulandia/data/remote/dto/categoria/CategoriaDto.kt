package com.domichav.perfulandia.data.remote.dto.categoria

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Representa un objeto de Categoria tal como lo DEVUELVE la API.
 */
data class CategoriaDto(

    @SerializedName("_id")
    val id: String,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("tipo")
    val tipo: String?,

    @SerializedName("descripcion")
    val descripcion: String?,

    @SerializedName("imagen")
    val imagen: String?,

    @SerializedName("imagenThumbnail")
    val imagenThumbnail: String?,

    @SerializedName("createdAt")
    val createdAt: Date,

    @SerializedName("updatedAt")
    val updatedAt: Date
)