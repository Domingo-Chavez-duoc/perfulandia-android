package com.domichav.perfulandia.data.remote.dto

import com.domichav.perfulandia.data.remote.dto.user.UserDto
import com.google.gson.annotations.SerializedName

/**
 * Represents the 'data' object received in a successful authentication response.
 * This class models the nested JSON object that contains the user details and the token.
 */
data class AuthPerfulandiaData(
    // This annotation links the 'user' field in the JSON to this property.
    @SerializedName("user")
    val user: UserDto,

    // This links the 'token' field in the JSON to the accessToken property.
    // It's common for the JSON key to be "token".
    @SerializedName("token")
    val accessToken: String
)

// CLASE CREADA POR NECESIDAD QUE PEDIA LA GUIA DE TEST, REVISAR
