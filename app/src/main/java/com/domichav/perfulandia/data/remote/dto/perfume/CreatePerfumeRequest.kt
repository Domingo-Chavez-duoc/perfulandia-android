package com.domichav.perfulandia.data.remote.dto.perfume

import com.google.gson.annotations.SerializedName

/**
 * DTO para la petición de creación de un nuevo perfume.
 * Coincide EXACTAMENTE con el 'CreatePerfumeDto' del backend de NestJS.
 */
data class CreatePerfumeRequest(
    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("descripcion")
    val descripcion: String?,

    @SerializedName("imagen")
    val imagen: String?,

    @SerializedName("imagenThumbnail")
    val imagenThumbnail: String?
)