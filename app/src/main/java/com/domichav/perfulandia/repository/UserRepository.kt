package com.domichav.perfulandia.repository

import android.app.Application
import com.domichav.perfulandia.data.local.SessionManager
import com.domichav.perfulandia.data.remote.RetrofitClient
import com.domichav.perfulandia.data.remote.api.AuthApiService
import com.domichav.perfulandia.data.remote.dto.SignupRequest
import com.domichav.perfulandia.data.remote.dto.SignupResponse
import com.domichav.perfulandia.data.remote.dto.UserResponse

/**
 * Repository for handling user-related operations, including session management.
 */
class UserRepository(application: Application) {

    // Create the API service using the app context
    private val authApiService: AuthApiService = RetrofitClient.create(application)
    private val sessionManager = SessionManager(application)

    /**
     * Registers a new user and saves the token upon success.
     */
    suspend fun register(request: SignupRequest): Result<SignupResponse> {
        return try {
            val response = authApiService.signup(request)
            // On successful signup, save the authentication token.
            sessionManager.saveAuthToken(response.authToken)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetches the current user's profile. The token is added automatically by the AuthInterceptor.
     */
    suspend fun getProfile(): Result<UserResponse> {
        return try {
            // The token is now handled by the interceptor, so we just call the method.
            val response = authApiService.getMe()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
