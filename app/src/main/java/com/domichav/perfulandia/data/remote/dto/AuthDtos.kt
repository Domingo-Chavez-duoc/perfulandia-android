package com.domichav.perfulandia.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object for the signup request body.
 */
data class SignupRequest(
    val name: String,
    val email: String,
    val password: String
)

/**
 * Data Transfer Object for the signup response body.
 */
data class SignupResponse(
    @SerializedName("authToken") val authToken: String,
    @SerializedName("user_id") val userId: Int
)

/**
 * Data Transfer Object for the user profile response body from /auth/me.
 */
data class UserResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String
    // Other fields like 'created_at', 'account_id', 'role' can be added here if needed.
)
