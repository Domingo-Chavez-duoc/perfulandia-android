package com.domichav.perfulandia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.domichav.perfulandia.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.net.Uri
import kotlinx.coroutines.flow.update
import com.domichav.perfulandia.repository.AvatarRepository

/**
 * Estado de la UI
 */
data class ProfileUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val userEmail: String = "",
    val error: String? = null,
    val avatarUri: Uri? = null
)

/**
 * ViewModel: Maneja la lógica de UI y el estado
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {


    // Pasa el contexto de la aplicación a UserRepository
    private val repository = UserRepository(application)
    private val avatarRepository = AvatarRepository(application)

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {

        // Carga user data al crear el ViewModel
        loadUser()

        // Suscribe a cambios en la URI del avatar
        viewModelScope.launch {
            avatarRepository.getAvatarUri().collect { uri ->
                _uiState.update { it.copy(avatarUri = uri) }
            }
        }
    }

    /**
     * Cargamos los datos del usuario desde la API utilizando el nuevo method getProfile
     */
    fun loadUser() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            // Llama al nuevo method getProfile() que no requiere un ID
            val result = repository.getProfile()

            result.fold(
                onSuccess = { user ->

                    // Utiliza el nombre del campo correcto de UserResponse
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userName = user.name,
                            userEmail = user.email
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Error desconocido"
                        )
                    }
                }
            )
        }
    }

    /**
     * Updates the URI of the user's avatar.
     */
    fun updateAvatar(uri: Uri?) {
        // Updatea el estado de la UI
        _uiState.update { it.copy(avatarUri = uri) }

        // Persistencia asíncrona del URI del avatar
        viewModelScope.launch {
            avatarRepository.saveAvatarUri(uri)
        }
    }
}
