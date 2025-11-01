package com.domichav.perfulandia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.domichav.perfulandia.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

//Camara
import android.net.Uri

/**
 * Estado de la UI
 */
data class ProfileUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val error: String? = null,
    val formattedCreatedAt: String = "",
    val avatarUri: Uri? = null  // ✨ Nuevo campo
)

/**
 * ViewModel: Maneja la lógica de UI y el estado
 * Usa AndroidViewModel para tener acceso al Application Context
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val dependencies = AppDependencies.getInstance(application)
    private val userRepository = dependencies.userRepository

    // ✨ Obtener AvatarRepository del contenedor
    private val avatarRepository = dependencies.avatarRepository

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
        loadSavedAvatar()  // ✨ Cargar avatar guardado al iniciar
    }

    // ✨ Nueva función: Cargar avatar desde DataStore
    private fun loadSavedAvatar() {
        viewModelScope.launch {
            avatarRepository.getAvatarUri().collect { savedUri ->
                _uiState.update { it.copy(avatarUri = savedUri) }
            }
        }
    }

    // ... resto del código ...
}

    /**
     * Actualiza la URI del avatar del usuario.
     */
    fun updateAvatar(uri: Uri?) {
        viewModelScope.launch {
            avatarRepository.saveAvatarUri(uri)
            // El estado se actualiza automáticamente vía Flow en loadSavedAvatar()
        }
    }