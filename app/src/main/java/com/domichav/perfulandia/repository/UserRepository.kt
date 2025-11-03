package com.domichav.perfulandia.repository

import android.app.Application
import android.util.Log
import com.domichav.perfulandia.data.local.SessionManager
import com.domichav.perfulandia.data.remote.RetrofitClient
import com.domichav.perfulandia.data.remote.api.AuthApiService
import com.domichav.perfulandia.data.remote.dto.LoginRequest
import com.domichav.perfulandia.data.remote.dto.LoginResponse
import com.domichav.perfulandia.data.remote.dto.SignupRequest
import com.domichav.perfulandia.data.remote.dto.SignupResponse
import com.domichav.perfulandia.data.remote.dto.UserResponse
import kotlinx.coroutines.flow.first

/**
 * Repository for handling user-related operations, including session management.
 */
class UserRepository(application: Application) {

    // Create the API service using the app context
    private val authApiService: AuthApiService = RetrofitClient.create(application)
    private val sessionManager = SessionManager(application)
    private val app = application

    private val TAG = "UserRepository"

    /**
     * Logs in an existing user via the remote auth endpoint and saves the received token.
     */
    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            val response = authApiService.login(request)

            val token = response.accessToken
            if (token.isNullOrEmpty()) {
                Log.w(TAG, "login: no accessToken in response")
                return Result.failure(Exception("No access token returned from login API"))
            }

            Log.d(TAG, "login: saving token=$token")
            sessionManager.saveAuthToken(token)
            val saved = sessionManager.authToken.first()
            Log.d(TAG, "login: saved token=$saved")

            Result.success(response)
        } catch (e: Exception) {
            Log.w(TAG, "login failed: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Registers a new user and saves the token upon success.
     */
    suspend fun register(request: SignupRequest): Result<SignupResponse> {
        return try {
            val response = authApiService.signup(request)

            // Some APIs return token in different fields. Prefer authToken, fallback to accessToken.
            val token = response.authToken ?: response.accessToken

            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No auth token returned from register API"))
            }

            Log.d(TAG, "register: saving token=$token")
            sessionManager.saveAuthToken(token)
            val saved = sessionManager.authToken.first()
            Log.d(TAG, "register: saved token=$saved")

            // On successful signup, save the authentication token.
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Fetches the current user's profile. The token is added automatically by the AuthInterceptor.
     * If the token is a local demo token (prefixed with 'local-token-'), return the profile
     * from local AccountRepository to avoid 401 from the remote API.
     */
    suspend fun getProfile(): Result<UserResponse> {
        return try {
            // Read current token synchronously
            val token = sessionManager.authToken.first()

            if (token != null && token.startsWith("local-token-")) {
                // Local token: extract the email and fetch the local account
                val email = token.removePrefix("local-token-")
                val accountRepo = AccountRepository(app)
                val accounts = accountRepo.getAllAccountsOnce()
                val account = accounts.firstOrNull { it.email.equals(email, ignoreCase = true) }

                if (account != null) {
                    // Map local Account to UserResponse
                    val localUser = UserResponse(
                        id = 0,
                        name = account.name,
                        email = account.email
                    )
                    return Result.success(localUser)
                } else {
                    return Result.failure(Exception("Local account not found for token"))
                }
            }

            // Otherwise call remote endpoint
            val response = authApiService.getMe()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
