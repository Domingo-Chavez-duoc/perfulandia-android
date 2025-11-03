package com.domichav.perfulandia.data.remote

import android.util.Log
import com.domichav.perfulandia.data.local.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

/**
 * AuthInterceptor: Añade automáticamente el token JWT a las peticiones
 *
 * Se ejecuta antes de la petición HTTP
 *
 * Uso:
 * 1. Recupera el token del SessionManager
 * 2. Si existe, añade el header: Authorization: Bearer {token}
 * 3. Si no existe, deja la petición sin modificar
 */
class AuthInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {

    private val TAG = "AuthInterceptor"

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Evitar añadir Authorization para los endpoints de autenticación (login/signup)
        val path = originalRequest.url.encodedPath
        if (path.contains("/auth/login") || path.contains("/auth/signup")) {
            Log.d(TAG, "Skipping auth header for path=$path")
            return chain.proceed(originalRequest)
        }

        // Recuperar el token (usando runBlocking porque intercept no es suspend)
        val token = runBlocking {
            sessionManager.authToken.first()
        }

        Log.d(TAG, "intercept: path=$path token=${token ?: "<null>"}")

        // Si no hay token, continuar con la petición original
        if (token.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }

        // Crear nueva petición con el token
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        // Continuar con la petición autenticada
        return chain.proceed(authenticatedRequest)
    }
}
