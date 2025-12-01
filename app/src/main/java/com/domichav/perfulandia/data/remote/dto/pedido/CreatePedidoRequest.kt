package com.domichav.perfulandia.data.remote.dto.pedido

import com.google.gson.annotations.SerializedName

/**

DTO para la petición de creación de un nuevo pedido.
 */
data class CreatePedidoRequest(
    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("descripcion")
    val descripcion: String?,

    @SerializedName("imagen")
    val imagen: String?,

    @SerializedName("imagenThumbnail")
    val imagenThumbnail: String?
)