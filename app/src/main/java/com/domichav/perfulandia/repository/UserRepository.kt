package com.example.actividad_2_5_2.repository

import android.content.Context
import com.domichav.perfulandia.data.remote.ApiService
import com.domichav.perfulandia.data.remote.RetrofitClient
import com.domichav.perfulandia.data.remote.dto.RegisterRequest
import com.domichav.perfulandia.data.remote.dto.UserDto

/**
 * Repository: Abstrae la fuente de datos
 * El ViewModel NO sabe si los datos vienen de API, base de datos local, etc.
 */
class UserRepository(context: Context) {

    private val apiService: ApiService = RetrofitClient
        .create(context)
        .create(ApiService::class.java)

    /**
     * Obtiene un usuario de la API
     *
     * Usa Result<T> para manejar éxito/error de forma elegante
     */
    suspend fun fetchUser(id: Int = 1): Result<UserDto> {
        return try {
            val user = apiService.getUserById(id)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Registra un nuevo usuario en la API
     */
    suspend fun register(request: RegisterRequest): Result<UserDto> {
        return try {
            val newUser = apiService.register(request)
            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}