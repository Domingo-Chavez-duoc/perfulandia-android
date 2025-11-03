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

    // Pass the application context to the UserRepository
    private val repository = UserRepository(application)
    private val avatarRepository = AvatarRepository(application)

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        // Load user data as soon as the ViewModel is created.
        loadUser()

        // Subscribe to avatar URI changes.
        viewModelScope.launch {
            avatarRepository.getAvatarUri().collect { uri ->
                _uiState.update { it.copy(avatarUri = uri) }
            }
        }
    }

    /**
     * Loads the user data from the API using the new getProfile method.
     */
    fun loadUser() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            // Call the new getProfile() method which doesn't require an ID.
            val result = repository.getProfile()

            result.fold(
                onSuccess = { user ->
                    // Use the `name` property from the UserResponse data class.
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
        // Update UI state immediately
        _uiState.update { it.copy(avatarUri = uri) }

        // Persist the avatar URI asynchronously
        viewModelScope.launch {
            avatarRepository.saveAvatarUri(uri)
        }
    }
}
