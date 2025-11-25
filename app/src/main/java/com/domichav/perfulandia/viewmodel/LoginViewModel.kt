package com.domichav.perfulandia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.domichav.perfulandia.data.remote.dto.LoginRequest
import com.domichav.perfulandia.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI State for the Login screen.
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel for the Login screen logic.
 * It uses AndroidViewModel to get the Application context, which is needed by UserRepository.
 */
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    // Manually instantiate UserRepository, just like your other ViewModels
    private val userRepository = UserRepository(application)

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * Manejo del boton de login
     * Crea un LoginRequest y llama al repositorio
     */
    fun onLoginClicked(email: String, password: String) {
        if (_uiState.value.isLoading) return

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            // Create the request object that the repository expects
            val loginRequest = LoginRequest(email = email, password = password)

            // Call the existing login method in your UserRepository
            val result = userRepository.login(loginRequest)

            result.fold(
                onSuccess = {
                    // On success, update UI state to trigger navigation
                    _uiState.update {
                        it.copy(isLoading = false, loginSuccess = true)
                    }
                },
                onFailure = { exception ->
                    // On failure, update UI state with the error
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Error en el login"
                        )
                    }
                }
            )
        }
    }

    /**
     * Reset del mensaje de error
     */
    fun onErrorMessageShown() {
        _uiState.update { it.copy(error = null) }
    }
}
