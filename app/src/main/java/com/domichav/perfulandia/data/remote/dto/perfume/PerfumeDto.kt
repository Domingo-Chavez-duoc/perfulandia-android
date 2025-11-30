package com.domichav.perfulandia.data.remote.dto.perfume

import com.google.gson.annotations.SerializedName

/**
 * Objeto de transferencia de datos (DTO) que representa un perfume.
 */

data class PerfumeDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("imagen")
    val imagen: String,  // URL de la imagen del perfume

    @SerializedName("imagenThumbnail")
    val imagenThumbnail: String,  // URL de la miniatura de la imagen del perfume
)