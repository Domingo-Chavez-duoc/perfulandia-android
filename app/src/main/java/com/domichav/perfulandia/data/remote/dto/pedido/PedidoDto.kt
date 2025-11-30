package com.domichav.perfulandia.data.remote.dto.pedido

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Representa un objeto de Pedido tal como lo devuelve la API.
 */
data class PedidoResponseDto(
    @SerializedName("_id")
    val id: String,

    @SerializedName("cliente")
    val clienteId: String,

    @SerializedName("items")
    val items: List<PedidoItemResponseDto>,

    @SerializedName("total")
    val total: Double,

    @SerializedName("estado")
    val estado: String?,

    @SerializedName("imagen")
    val imagen: String?,

    @SerializedName("imagenThumbnail")
    val imagenThumbnail: String?,

    @SerializedName("createdAt")
    val createdAt: Date,

    @SerializedName("updatedAt")
    val updatedAt: Date
)

/**
 * Representa un item dentro de la respuesta de un pedido.
 */
data class PedidoItemResponseDto(
    @SerializedName("perfume")
    val perfumeId: String,

    @SerializedName("cantidad")
    val cantidad: Int,

    @SerializedName("precio")
    val precio: Double
)