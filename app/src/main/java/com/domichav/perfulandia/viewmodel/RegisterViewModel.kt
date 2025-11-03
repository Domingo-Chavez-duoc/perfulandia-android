package com.domichav.perfulandia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.domichav.perfulandia.data.local.Account
import com.domichav.perfulandia.repository.AccountRepository
import com.domichav.perfulandia.data.remote.dto.SignupRequest
import com.domichav.perfulandia.data.remote.dto.SignupResponse
import com.domichav.perfulandia.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de la UI para la pantalla de registro
 */
data class RegisterUiState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel para la pantalla de registro
 */
class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    // Pass the application context to the UserRepository
    private val repository = UserRepository(application)
    private val accountRepository = AccountRepository(application)

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    /**
     * Registra un nuevo usuario
     * @param name The user's full name.
     * @param email The user's email address.
     * @param password The user's chosen password.
     */
    fun registerUser(name: String, email: String, password: String) {
        // Set loading state
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            // Create the request object for the API
            val request = SignupRequest(name = name, email = email, password = password)

            // Call the repository
            val result: Result<SignupResponse> = repository.register(request)


            // Update UI state based on the result from the repository
            result.fold(
                onSuccess = {

                    accountRepository.saveAccount(Account(name = name, email = email, password = password))

                    // On success, we have saved the token via the repository. Just indicate success.
                    _uiState.update { it.copy(isLoading = false, success = true) }
                },
                onFailure = { exception ->
                    // On failure, update the state with the error message.
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
}
