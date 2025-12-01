package com.domichav.perfulandia.data.remote.dto.resena

import com.google.gson.annotations.SerializedName

/**

DTO para la petición de creación de un nueva resena.
*/
data class CreateResenaRequest(
    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("descripcion")
    val descripcion: String?,

    @SerializedName("imagen")
    val imagen: String?,

    @SerializedName("imagenThumbnail")
    val imagenThumbnail: String?
)

