
package com.domichav.perfulandia.data.remote.dto.resena

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Representa un objeto de Reseña (Review) tal como lo DEVUELVE la API.
 */
data class ResenaDto(
    @SerializedName("_id")
    val id: String,

    @SerializedName("perfume")
    val perfumeId: String,

    @SerializedName("cliente")
    val clienteId: String,

    @SerializedName("puntuacion")
    val puntuacion: Int,

    @SerializedName("comentario")
    val comentario: String?,

    @SerializedName("imagen")
    val imagen: String?,

    @SerializedName("imagenThumbnail")
    val imagenThumbnail: String?,

    @SerializedName("createdAt")
    val createdAt: Date,

    @SerializedName("updatedAt")
    val updatedAt: Date
)