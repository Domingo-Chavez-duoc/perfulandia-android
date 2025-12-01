package com.domichav.perfulandia.data.remote.dto.resena

import com.google.gson.annotations.SerializedName

/**
 * DTO para la petición de actualización de una reseña existente.
 * Los campos son opcionales, permitiendo actualizaciones parciales.
 */
data class UpdateResenaDto(
    @SerializedName("puntuacion")
    val puntuacion: Int?,

    @SerializedName("comentario")
    val comentario: String?,

    // Estos campos también podrían ser actualizables, pero normalmente se manejan
    // a través del endpoint de subida de imagen. Los dejo comentados por si los necesitas.
    // @SerializedName("imagen")
    // val imagen: String?,
    //
    // @SerializedName("imagenThumbnail")
    // val imagenThumbnail: String?
)
