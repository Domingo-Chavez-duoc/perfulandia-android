package com.domichav.perfulandia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.domichav.perfulandia.data.remote.dto.LoginRequest
import com.domichav.perfulandia.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
class LoginViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    /**
     * Manejo del boton de login
     * Crea un LoginRequest y llama al repositorio
     */
    fun onLoginClicked(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(error = "Email y contraseña no pueden estar vacíos") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val request = LoginRequest(email = email, password = password)
            val result = userRepository.login(request) // Ahora llamamos con el objeto correcto

            result.fold(
                onSuccess = {
                    _uiState.update { state -> state.copy(isLoading = false, loginSuccess = true) }
                },
                onFailure = { exception ->
                    // On failure, update UI state with the error
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            loginSuccess = false,
                            error = exception.message ?: "Error desconocido"
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

class LoginViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            // La Factory se encarga de crear el UserRepository real
            val repository = UserRepository(application)
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}