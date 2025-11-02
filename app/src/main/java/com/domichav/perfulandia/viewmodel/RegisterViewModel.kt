package com.domichav.perfulandia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.domichav.perfulandia.data.remote.dto.RegisterRequest
import com.example.actividad_2_5_2.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val repository = UserRepository(application)

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    /**
     * Registra un nuevo usuario
     */
    fun registerUser(username: String, email: String, password: String) {
        _uiState.value = RegisterUiState(isLoading = true)

        viewModelScope.launch {
            val request = RegisterRequest(username, email, password)
            val result = repository.register(request)

            _uiState.value = result.fold(
                onSuccess = { RegisterUiState(success = true) },
                onFailure = { exception ->
                    RegisterUiState(error = exception.localizedMessage ?: "Error desconocido")
                }
            )
        }
    }
}