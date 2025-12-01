package com.domichav.perfulandia.data.remote.dto.categoria

import com.google.gson.annotations.SerializedName

/**
 * DTO para la petición de creación de una nueva categoria.
 * Coincide EXACTAMENTE con el 'CreateCategoriaDto' del backend de NestJS.
 */
data class CreateCategoriaRequest(
    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("descripcion")
    val descripcion: String?,

    @SerializedName("imagen")
    val imagen: String?,

    @SerializedName("imagenThumbnail")
    val imagenThumbnail: String?
)