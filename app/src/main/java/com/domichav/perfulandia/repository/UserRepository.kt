package com.domichav.perfulandia.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.domichav.perfulandia.data.local.SessionManager
import com.domichav.perfulandia.data.remote.RetrofitClient
import com.domichav.perfulandia.data.remote.api.UserApiService
import com.domichav.perfulandia.data.remote.dto.LoginRequest
import com.domichav.perfulandia.data.remote.dto.LoginResponse
import com.domichav.perfulandia.data.remote.dto.RegisterRequest
import com.domichav.perfulandia.data.remote.dto.user.UserDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Repository para el manejo de operaciones relacionadas con los usuarios, como el manejo de sesiones. (session management)
 *
 * *** CÓDIGO CORREGIDO Y ALINEADO CON LA API REAL DE NESTJS ***
 */
class UserRepository(private val context: Context) {

    // --- CORREGIDO: Renombrada la variable para mayor claridad. Usa la instancia de Retrofit. ---
    private val userApiService: UserApiService = RetrofitClient.createService(context, UserApiService::class.java)
    private val sessionManager = SessionManager(context)

    private val TAG = "UserRepository"

    /**
     * Logea un usuario existente y guarda el token JWT recibido.
     */
    suspend fun login(request: LoginRequest): Result<LoginResponse> {
        return try {
            val apiResponse = userApiService.login(request)
            val loginData = apiResponse.data

            // --- CORRECCIÓN CLAVE: Verificar si 'data' es nulo ---
            if (loginData == null) {
                Log.w(TAG, "login: response data is null; apiResponse=$apiResponse")
                return Result.failure(Exception("No data returned from login API"))
            }

            val token = loginData.accessToken ?:""

            if (token.isNullOrEmpty()) {
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
            val apiResponse = userApiService.register(request)
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
    suspend fun getProfile(): Result<UserDto> {
        return try {
            val apiResponse = userApiService.getMyProfile()
            val profileData = apiResponse.data

            if (profileData == null) {
                Log.w(TAG, "getProfile: response data is null; apiResponse=$apiResponse")
                return Result.failure(Exception("No profile data returned from API"))
            }

            Result.success(profileData)
        } catch (e: Exception) {
            Log.w(TAG, "getProfile failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun uploadAvatar(uri: Uri): Result<UserDto> {
        return try {
            // Usa el ContentResolver para obtener un flujo de entrada (InputStream) del Uri.
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return Result.failure(Exception("No se pudo abrir el archivo desde el Uri."))

            // Lee los bytes del archivo.
            val fileBytes = inputStream.readBytes()
            inputStream.close()

            // Crea un RequestBody con los bytes del archivo y su tipo MIME.
            val requestFile = fileBytes.toRequestBody(
                context.contentResolver.getType(uri)?.toMediaTypeOrNull()
            )

            // Crea el MultipartBody.Part, que es lo que Retrofit necesita.
            // El nombre "file" debe coincidir con el que espera tu backend (en el FileInterceptor).
            val body = MultipartBody.Part.createFormData("file", "avatar.jpg", requestFile)

            val response = userApiService.uploadUserAvatar(body)
            if (response.data == null) {
                return Result.failure(Exception("La respuesta de la subida del avatar no contenía datos del usuario."))
            }

            Result.success(response.data)
        } catch (e: Exception) {
            Log.e(TAG, "Error al subir el avatar: ${e.message}", e)
            Result.failure(e)
        }
    }
}
