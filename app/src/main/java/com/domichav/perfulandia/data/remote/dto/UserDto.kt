package com.domichav.perfulandia.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Este objeto representa los datos que VIAJAN entre nuestra app y el servidor
 */
data class UserDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("role")
    val role: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("location")
    val location: String,

    @SerializedName("address")
    val address: String,

    @SerializedName("firstName")
    val firstName: String,

    @SerializedName("lastName")
    val lastName: String,

    @SerializedName("image")
    val image: String? = null  // URL de imagen de perfil
)