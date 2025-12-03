package com.domichav.perfulandia.data.remote.dto.perfume

import com.domichav.perfulandia.data.remote.dto.categoria.CategoriaDto
import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * Representa un Perfume cuando la API devuelve la 'categoria' como un objeto completo (populada).
 * Usado en getPerfumeById y filterPerfumes.
 */
data class PerfumePopulatedDto(
    @SerializedName("_id")
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
        val tamaño: Int,

    @SerializedName("fragancia")
        val fragancia: String,

    // --- LA DIFERENCIA CLAVE ---
        @SerializedName("categoria")
        val categoria: CategoriaDto, // Aquí esperamos el objeto CategoriaDto

    @SerializedName("imagen")
        val imagen: String?,

    @SerializedName("imagenThumbnail")
        val imagenThumbnail: String?,

    @SerializedName("createdAt")
        val createdAt: Date,

    @SerializedName("updatedAt")
        val updatedAt: Date
)

