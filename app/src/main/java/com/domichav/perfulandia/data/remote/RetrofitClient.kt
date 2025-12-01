package com.domichav.perfulandia.data.remote

import android.content.Context
import com.domichav.perfulandia.data.local.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton para gestionar la creación y configuración del cliente Retrofit.
 */
object RetrofitClient {

    // URL base de tu API. Asegúrate de que apunte a tu backend en Render cuando esté desplegado.
    // Para desarrollo local con emulador, usa 10.0.2.2.
    private const val BASE_URL = "http://10.0.2.2:3000/"

    // Variable para mantener la instancia de ApiService
    @Volatile
    private var apiService: ApiService? = null

    /**
     * Obtiene una instancia configurada de ApiService.
     * Si no existe, la crea de forma segura (thread-safe).
     *
     * @param context El contexto de la aplicación, necesario para inicializar dependencias.
     * @return Una instancia de ApiService.
     */
    fun getInstance(context: Context): ApiService {
        // Doble-verificación para asegurar la creación de una única instancia (thread-safe)
        return apiService ?: synchronized(this) {
            apiService ?: buildApiService(context).also { apiService = it }
        }
    }

    /**
     * Construye y configura el ApiService con OkHttpClient y Retrofit.
     */
    private fun buildApiService(context: Context): ApiService {
        // 1. Inicializar SessionManager
        val sessionManager = SessionManager(context)

        // 2. Configurar el logging interceptor para depuración
        // Imprime en Logcat los detalles de las peticiones y respuestas (headers, body, etc.)
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // 3. Configurar el cliente OkHttp para añadir interceptores
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionManager)) // <--- ¡AQUÍ ESTÁ LA MAGIA!
            .addInterceptor(loggingInterceptor)              // Añadimos el logger
            .connectTimeout(30, TimeUnit.SECONDS)          // Aumentar timeouts
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        // 4. Construir Retrofit con el cliente OkHttp personalizado
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // <--- Usamos el cliente OkHttp configurado
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // 5. Crear y devolver la implementación de ApiService
        return retrofit.create(ApiService::class.java)
    }
}
