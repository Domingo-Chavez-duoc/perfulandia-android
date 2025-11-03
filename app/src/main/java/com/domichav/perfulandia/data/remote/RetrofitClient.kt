package com.domichav.perfulandia.data.remote

import android.content.Context
import com.domichav.perfulandia.data.local.SessionManager
import com.domichav.perfulandia.data.remote.api.AuthApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton object to provide a configured Retrofit instance.
 */
object RetrofitClient {

    private const val BASE_URL = "https://x8ki-letl-twmt.n7.xano.io/api:Rfm_61dW/"

    /**
     * Creates and configures a Retrofit service instance.
     * @param context The application context, used for initializing the SessionManager.
     * @return A fully configured instance of [AuthApiService].
     */
    fun create(context: Context): AuthApiService {
        // 1. SessionManager to get the token
        val sessionManager = SessionManager(context)

        // 2. AuthInterceptor to add the token to headers
        val authInterceptor = AuthInterceptor(sessionManager)

        // 3. Logging Interceptor for debugging
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // 4. OkHttpClient with both interceptors
        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor) // Automatically adds the token
            .addInterceptor(loggingInterceptor) // Logs the request with the token
            .build()

        // 5. Retrofit instance using the configured client
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // 6. Create the API service
        return retrofit.create(AuthApiService::class.java)
    }
}
