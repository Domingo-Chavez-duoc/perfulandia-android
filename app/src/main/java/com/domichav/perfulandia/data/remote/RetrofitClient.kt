package com.domichav.perfulandia.data.remote

import android.content.Context
import com.domichav.perfulandia.data.local.SessionManager
import com.domichav.perfulandia.data.remote.api.AuthApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Objeto singleton para proporcionar una instancia de Retrofit configurada
 */
object RetrofitClient {

    private const val BASE_URL = "https://x8ki-letl-twmt.n7.xano.io/api:Rfm_61dW/"

    /**
     * Crea y configura una instancia de servicio Retrofit
     * @param context El contexto de la aplicación se utiliza para iniciar el SessionManager
     * @return Una configuración completa de la instancia [AuthApiService].
     */
    fun create(context: Context): AuthApiService {
        // 1. SesssionManager para obtener el token
        val sessionManager = SessionManager(context)

        // 2, AuthInterceptor para agregar el token a los headers
        val authInterceptor = AuthInterceptor(sessionManager)

        //3. Loggin Interceptor para depuración (debugging)
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // 5. OkHttpClient con ambos interceptors
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor) //añade automaticamente el token
            .addInterceptor(loggingInterceptor) // Logea la peticion con el token
            .build()

        // 5. Instancia de Retrofit con el cliente configurado
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // 6. Crea el Api Service con la configuración de Retrofit
        return retrofit.create(AuthApiService::class.java)
    }
}
