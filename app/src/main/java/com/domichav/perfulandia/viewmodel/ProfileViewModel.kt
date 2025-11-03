package com.domichav.perfulandia.viewmodel  // ⚠️ Cambia esto por tu paquete

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.actividad_2_5_2.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.net.Uri
import kotlinx.coroutines.flow.update
import com.domichav.perfulandia.repository.AvatarRepository
import kotlinx.coroutines.flow.collect

/**
 * Estado de la UI
 */
data class ProfileUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val userEmail: String = "",
    val error: String? = null,
    val avatarUri: Uri? = null,
    val formattedCreatedAt: String = ""
)

/**
 * ViewModel: Maneja la lógica de UI y el estado
 * Usa AndroidViewModel para tener acceso al Application Context
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UserRepository(application)
    // Avatar persistence repository
    private val avatarRepository = AvatarRepository(application)

    // Estado PRIVADO (solo el ViewModel lo modifica)
    private val _uiState = MutableStateFlow(ProfileUiState())

    // Estado PÚBLICO (la UI lo observa)
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        // Suscribirse al URI guardado del avatar y actualizar el estado cuando cambie
        viewModelScope.launch {
            avatarRepository.getAvatarUri().collect { uri ->
                _uiState.update { it.copy(avatarUri = uri) }
            }
        }
    }

    /**
     * Carga los datos del usuario desde la API
     */
    fun loadUser(id: Int = 1) {
        // Indicar que está cargando
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null
        )

        // Ejecutar en coroutine (no bloquea la UI)
        viewModelScope.launch {
            val result = repository.fetchUser(id)

            // Actualizar el estado según el resultado
            _uiState.value = result.fold(
                onSuccess = { user ->
                    // ✅ Éxito: mostrar datos
                    _uiState.value.copy(
                        isLoading = false,
                        userName = user.username,
                        userEmail = user.email, // user.email is non-nullable in DTO
                        error = null
                    )
                },
                onFailure = { exception ->
                    // ❌ Error: mostrar mensaje
                    _uiState.value.copy(
                        isLoading = false,
                        error = exception.localizedMessage ?: "Error desconocido"
                    )
                }
            )
        }
    }
    /**
     * Actualiza la URI del avatar del usuario.
     */
    fun updateAvatar(uri: Uri?) {
        // Update UI state immediately
        _uiState.update { it.copy(avatarUri = uri) }

        // Persist the avatar URI asynchronously
        viewModelScope.launch {
            avatarRepository.saveAvatarUri(uri)
        }
    }
}