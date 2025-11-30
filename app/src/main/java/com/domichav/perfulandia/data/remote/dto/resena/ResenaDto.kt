package com.domichav.perfulandia.data.remote.dto.resena

import com.google.gson.annotations.SerializedName

data class ResenaDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("usuario")
    val usuario: String,

    @SerializedName("calificacion")
    val calificacion: Int,

    @SerializedName("comentario")
    val comentario: String,

    @SerializedName("fecha")
    val fecha: String // Fecha en formato ISO 8601
)