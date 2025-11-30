package com.domichav.perfulandia.data.remote.dto.pedido

import com.google.gson.annotations.SerializedName

/**
 * DTO para la petición de creación de un nuevo pedido. * Corresponde a la clase CreatePedidoDto en el backend de NestJS.
 * Los campos opcionales pueden ser nulos si no se envían.
 */
data class CreatePedidoRequest(
    @SerializedName("cliente")
    val cliente: String?,

    @SerializedName("items")
    val items: List<PedidoItemDto>,

    @SerializedName("direccionEntrega")
    val direccionEntrega: String?,

    @SerializedName("notasEntrega")
    val notasEntrega: String?
)

data class PedidoItemDto(
    @SerializedName("producto")
    val producto: String,

    @SerializedName("cantidad")
    val cantidad: Int
)