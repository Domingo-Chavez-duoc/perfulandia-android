package com.domichav.perfulandia.data.remote.dto.perfume

import com.google.gson.annotations.SerializedName
import java.util.Date

data class PerfumeDto(
    @SerializedName("_id") // En NestJS/MongoDB se usa _id
    val id: String,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("marca")
    val marca: String,

    @SerializedName("descripcion")
    val descripcion: String?,

    @SerializedName("precio")
    val precio: Double,

    @SerializedName("stock")
    val stock: Int,

    @SerializedName("genero")
    val genero: String,

    @SerializedName("tamaño")
    val tamaño: String,

    @SerializedName("fragancia")
    val fragancia: String,

    @SerializedName("categoriaId")
    val categoriaId: String,

    @SerializedName("imagen")
    val imagen: String?,

    @SerializedName("imagenThumbnail")
    val imagenThumbnail: String?,

    @SerializedName("createdAt")
    val createdAt: Date,

    @SerializedName("updatedAt")
    val updatedAt: Date
)