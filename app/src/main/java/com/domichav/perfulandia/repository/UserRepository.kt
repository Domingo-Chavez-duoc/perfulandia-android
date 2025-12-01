package com.domichav.perfulandia.repository

import android.app.Application
import android.util.Log
import com.domichav.perfulandia.data.local.SessionManager
import com.domichav.perfulandia.data.remote.ApiService
import com.domichav.perfulandia.data.remote.RetrofitClient
import com.domichav.perfulandia.data.remote.dto.LoginRequest
import com.domichav.perfulandia.data.remote.dto.RegisterRequest
import com.domichav.perfulandia.data.remote.dto.LoginResponse
import com.domichav.perfulandia.data.remote.dto.cliente.ClienteProfileDto
import com.domichav.perfulandia.data.remote.dto.user.UserDto

/**
 * Repository para el manejo de operaciones relacionadas con los usuarios, como el manejo de sesiones. (session management)
 *
 * *** CÓDIGO CORREGIDO Y ALINEADO CON LA API REAL DE NESTJS ***
 */
class UserRepository(application: Application) {

    // --- CORREGIDO: Renombrada la variable para mayor claridad. Usa la instancia de Retrofit. ---
    private val apiService: ApiService = RetrofitClient.create(application)
    private val sessionManager = SessionManager(application)
    // Eliminada la variable 'app' porque ya no se usa la lógica local del AccountRepository.

    private val TAG = "UserRepository"

    /**
     * Logea un usuario existente y guarda el token JWT recibido.
     */
    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            val apiResponse = apiService.login(request)
            val loginData = apiResponse.data

            // --- CORRECCIÓN CLAVE: Verificar si 'data' es nulo ---
            if (loginData == null) {
                Log.w(TAG, "login: response data is null; apiResponse=$apiResponse")
                return Result.failure(Exception("No data returned from login API"))
            }

            val token = loginData.accessToken

            if (token.isEmpty()) {
                Log.w(TAG, "login: no token in response; response=$loginData")
                return Result.failure(Exception("No auth token returned from login API"))
            }

            Log.d(TAG, "login: saving token=$token")
            sessionManager.saveAuthToken(token)

            sessionManager.saveUserEmail(request.email)

            // Ahora el compilador sabe que loginData no es nulo aquí.
            Result.success(loginData)
        } catch (e: Exception) {
            Log.w(TAG, "login failed: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Registra un nuevo usuario. El backend no devuelve un token en el registro,
     * por lo que el usuario deberá hacer login después.
     */
    suspend fun register(request: RegisterRequest): Result<UserDto> {
        return try {
            val apiResponse = apiService.register(request)
            val userData = apiResponse.data

            // --- CORRECCIÓN CLAVE: Verificar si 'data' es nulo ---
            if (userData == null) {
                Log.w(TAG, "register: response data is null; apiResponse=$apiResponse")
                return Result.failure(Exception("No data returned from register API"))
            }

            Log.d(TAG, "register: successful, user created=${userData.email}")

            // Ahora el compilador sabe que userData no es nulo aquí.
            Result.success(userData)
        } catch (e: Exception) {
            Log.w(TAG, "register failed: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Busca (fetch) el perfil del usuario actual (ClienteProfile).
     * El token se agrega automáticamente por medio del AuthInterceptor.
     */
    suspend fun getProfile(): Result<ClienteProfileDto> {
        return try {
            val apiResponse = apiService.getMyProfile()
            val profileData = apiResponse.data

            // --- CORRECCIÓN CLAVE: Verificar si 'data' es nulo ---
            if (profileData == null) {
                Log.w(TAG, "getProfile: response data is null; apiResponse=$apiResponse")
                return Result.failure(Exception("No profile data returned from API"))
            }

            // Ahora el compilador sabe que profileData no es nulo aquí.
            Result.success(profileData)
        } catch (e: Exception) {
            Log.w(TAG, "getProfile failed: ${e.message}")
            Result.failure(e)
        }
    }
}
