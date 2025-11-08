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
 * Repository para el manejo de operaciones relacionadas con los usuarios, como el manejo de sesiones. (session management)
 */
class UserRepository(application: Application) {

    // Create the API service using the app context
    private val authApiService: AuthApiService = RetrofitClient.create(application)
    private val sessionManager = SessionManager(application)
    private val app = application

    private val TAG = "UserRepository"

    /**
     * Logea un usuario existente a través del endpoint de autenticación remota (remote auth) y guarda el token recibido.
     */
    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            val response = authApiService.login(request)

            // Prefer authToken (README indicates server returns authToken) then fallback to accessToken
            val token = response.authToken ?: response.accessToken

            if (token.isNullOrEmpty()) {
                Log.w(TAG, "login: no token in response; response=$response")
                return Result.failure(Exception("No auth token returned from login API"))
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
     * Registra un nuevo usuario y guarda el token en caso de éxito
     */
    suspend fun register(request: SignupRequest): Result<SignupResponse> {
        return try {
            val response = authApiService.signup(request)

            // Algunos APIs devuelven el token en diferentes campos. Prefiere authToken por sobre accessToken
            val token = response.authToken ?: response.accessToken

            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("No auth token returned from register API"))
            }

            Log.d(TAG, "register: saving token=$token")
            sessionManager.saveAuthToken(token)
            val saved = sessionManager.authToken.first()
            Log.d(TAG, "register: saved token=$saved")

            // Si el signup es exitoso, guarda el token de autenticación
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Busca (fetch) el perfil del usuario actual. El token se agrega automáticamente por medio del AuthInterceptor
     * Si el token es demo local (prefijo 'local-token-'), devuelve el profile del usuario.
     * Desde el local AccountRepository para evitar 401 de la API remota.
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
                    // Clear the stale local token so the app can recover to a login state
                    try {
                        sessionManager.saveAuthToken("")
                    } catch (_: Exception) {
                        // ignore
                    }
                    Log.w(TAG, "getProfile: local token present but account not found for email=$email; cleared token")
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
