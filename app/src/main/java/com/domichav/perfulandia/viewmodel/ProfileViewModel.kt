package com.domichav.perfulandia.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.domichav.perfulandia.data.local.SessionManager
import com.domichav.perfulandia.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de la UI para el perfil de usuario.
 * Simplificado para reflejar la API real.
 */
data class ProfileUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val userEmail: String = "",
    val error: String? = null,
    // Este campo ahora representará la URL del avatar que viene del backend.
    val avatarUrl: String? = null
)

/**
 * ViewModel que maneja la lógica de la pantalla de perfil.
 * *** CÓDIGO CORREGIDO: ALINEADO CON LA API REAL DE NESTJS ***
 */
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    // --- CORREGIDO: Solo se necesitan estos dos repositorios ---
    private val userRepository = UserRepository(application)
    private val sessionManager = SessionManager(application)
    // --- ELIMINADO: `AccountRepository` y `AvatarRepository` ya no son necesarios.

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        // Al iniciar, cargamos los datos del usuario desde nuestra API real.
        loadUserProfile()
    }

    /**
     * Carga los datos del perfil:
     * - Nombre y URL del avatar desde la API (a través de ClienteProfile y User).
     * - Email desde la sesión local (guardado durante el login).
     */
    fun loadUserProfile() {
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            // Se obtienen ambas piezas de información en paralelo para eficiencia.
            val profileResult = userRepository.getProfile()
            val userEmail = sessionManager.userEmail.first() ?: "Email no encontrado"

            // --- LÓGICA DE UI SIMPLIFICADA ---
            // Se elimina toda la lógica compleja de `AccountRepository` y manejo de archivos locales.
            // Ahora solo actualizamos el estado con los datos que vienen de la API y la sesión.
            profileResult.fold(
                onSuccess = { clienteProfile ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            userName = clienteProfile.nombre,
                            userEmail = userEmail
                            // TODO: Cuando el backend devuelva el User asociado, aquí pondríamos:
                            // avatarUrl = clienteProfile.user.avatar
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Error desconocido al cargar el perfil"
                        )
                    }
                }
            )
        }
    }

    /**
     * Actualiza el avatar del usuario.
     * Esta función ahora es un placeholder para la lógica correcta.
     */
    fun updateAvatar(uri: Uri?) {
        // --- LÓGICA DE UI SIMPLIFICADA ---
        // Se elimina toda la lógica de `copyUriToInternalStorage` y `AccountRepository`.
        // TODO: La lógica correcta aquí sería:
        // 1. Convertir la `uri` de la imagen a un `MultipartBody.Part`.
        // 2. Crear una nueva función en `ApiService`: `uploadAvatar(file: MultipartBody.Part)`.
        // 3. Crear una nueva función en `UserRepository` que llame a ese endpoint.
        // 4. Llamar a `userRepository.uploadAvatar(file)` desde aquí.
        // 5. Al tener éxito, el backend actualiza la URL y podemos llamar a `loadUserProfile()` para refrescar.

        // Por ahora, no hacemos nada para evitar introducir lógica incorrecta.
        _uiState.update { it.copy(error = "La actualización de avatar aún no está implementada.") }
    }
}
