package com.domichav.perfulandia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.domichav.perfulandia.data.local.Account
import com.domichav.perfulandia.repository.AccountRepository
import com.domichav.perfulandia.data.remote.dto.LoginRequest
import com.domichav.perfulandia.data.remote.dto.RegisterRequest
import com.domichav.perfulandia.data.remote.dto.user.UserDto
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
    val error: String? = null,
    val registeredEmail: String? = null
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
     * @param name Nombre completo de usuario
     * @param email Email del usuario
     * @param password Contraseña del usuario
     */
    fun registerUser(name: String, email: String, password: String) {
        // Setea un loading state
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            // Crea una solicitud para la API
            val request = RegisterRequest(nombre = name, email = email, password = password)

            // Llama al repository
            val result: Result<UserDto> = repository.register(request)


            //Updatea el estado de la UI basado en el resultado del repository
            result.fold(
                onSuccess = {
                    val normalizedEmail = email.trim().lowercase()
                    accountRepository.saveAccount(Account(name = name, email = normalizedEmail, password = password))

                    //On success, tenemos guardado el token a través del repository. Indicar success y email registrado
                    _uiState.update { it.copy(isLoading = false, success = true, registeredEmail = normalizedEmail) }
                },
                onFailure = { exception ->
                    // On failure, updatea el estado de la UI con el error message
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
