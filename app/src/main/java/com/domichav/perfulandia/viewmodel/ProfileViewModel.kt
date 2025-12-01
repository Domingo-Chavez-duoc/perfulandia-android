package com.domichav.perfulandia.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewModelScope
import com.domichav.perfulandia.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false,
    val userName: String = "", // Este vendrá del ClienteProfile, pero por ahora lo dejamos simple
    val userEmail: String = "",
    val error: String? = null,
    val avatarUrl: String? = null
)

class ProfileViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadUserProfile()
    }

    /**
     * Carga los datos del perfil del usuario (email y avatar) desde el endpoint de User.
     */
    fun loadUserProfile() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val profileResult = userRepository.getProfile() // Esto ahora devuelve Result<UserDto>

            profileResult.fold(
                onSuccess = { user ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            // TODO: El 'nombre' vive en una entidad separada (ClienteProfile).
                            // Por ahora usamos el email como placeholder del nombre.
                            userName = user.email.substringBefore('@'),
                            userEmail = user.email,
                            avatarUrl = user.avatar
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Error al cargar el perfil"
                        )
                    }
                }
            )
        }
    }

    /**
     * Actualiza el avatar del usuario.
     */
    fun updateAvatar(uri: Uri?) {
        if (uri == null) return
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = userRepository.uploadAvatar(uri)

            result.fold(
                onSuccess = { updatedUser ->
                    // Éxito: el backend devolvió el UserDto con la nueva URL
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            avatarUrl = updatedUser.avatar, // Actualizamos la UI
                            error = null
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Error al actualizar el avatar: ${exception.message}"
                        )
                    }
                }
            )
        }
    }
}

class ProfileViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            // Aquí es donde se crea la dependencia real
            val repository = UserRepository(application)
            // Y aquí se la pasamos al ViewModel
            return ProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class for ProfileViewModel")
    }
}