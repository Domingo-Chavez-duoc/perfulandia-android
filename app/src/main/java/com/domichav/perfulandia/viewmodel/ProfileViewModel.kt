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
import com.domichav.perfulandia.repository.AccountRepository
import com.domichav.perfulandia.utils.copyUriToInternalStorage
import kotlinx.coroutines.flow.first
import java.io.File

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


    // Pasa el contexto de la aplicación a repositories
    private val repository = UserRepository(application)
    private val avatarRepository = AvatarRepository(application)
    private val accountRepository = AccountRepository(application)

    private val app: Application = application

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {

        // Carga user data al crear el ViewModel
        loadUser()

        // Suscribe a cambios en la URI del avatar
        // Keep legacy subscription for backwards compatibility (single avatar key)
        viewModelScope.launch {
            avatarRepository.getLegacyAvatarUri().collect { uri ->
                // Only set legacy avatar if we don't have a per-account avatar yet
                _uiState.update { if (it.avatarUri == null) it.copy(avatarUri = uri) else it }
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
                    // After we have the user's email, try to load per-account avatar from AccountRepository
                    viewModelScope.launch {
                        try {
                            // 1) Check AccountRepository for stored avatarPath
                            val accounts = accountRepository.getAllAccountsOnce()
                            val account = accounts.firstOrNull { it.email.equals(user.email, ignoreCase = true) }
                            if (account?.avatarPath != null) {
                                // Use internal file path
                                val file = File(account.avatarPath)
                                if (file.exists()) {
                                    _uiState.update { it.copy(avatarUri = Uri.fromFile(file)) }
                                } else {
                                    // File missing: clear stored avatarPath
                                    accountRepository.updateAccountAvatar(user.email, null)
                                    _uiState.update { it.copy(avatarUri = null) }
                                }
                            } else {
                                // 2) no per-account avatar: try legacy avatar and migrate it to internal storage
                                val legacy = avatarRepository.getLegacyAvatarUri().first()
                                if (legacy != null) {
                                    // Attempt to copy legacy URI content into app internal storage
                                    val sanitized = user.email.replace("[^A-Za-z0-9]".toRegex(), "_")
                                    val targetName = "avatar_${sanitized}.jpg"
                                    val copiedPath = copyUriToInternalStorage(app, legacy, targetName)
                                    if (copiedPath != null) {
                                        accountRepository.updateAccountAvatar(user.email, copiedPath)
                                        _uiState.update { it.copy(avatarUri = Uri.fromFile(File(copiedPath))) }
                                    } else {
                                        _uiState.update { it.copy(avatarUri = null) }
                                    }
                                } else {
                                    _uiState.update { it.copy(avatarUri = null) }
                                }
                            }
                        } catch (e: Exception) {
                            // ignore and continue without avatar
                            _uiState.update { it.copy(avatarUri = null) }
                        }
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
            val email = _uiState.value.userEmail
            if (uri == null) {
                // Clear avatar for this account
                if (email.isNotBlank()) {
                    // delete stored file if any
                    val accounts = accountRepository.getAllAccountsOnce()
                    val account = accounts.firstOrNull { it.email.equals(email, ignoreCase = true) }
                    account?.avatarPath?.let { path ->
                        try { File(path).delete() } catch (_: Exception) {}
                    }
                    accountRepository.updateAccountAvatar(email, null)
                } else {
                    avatarRepository.saveLegacyAvatarUri(null)
                }
            } else {
                // Copy the provided URI into internal storage and save path in Account
                if (email.isNotBlank()) {
                    val sanitized = email.replace("[^A-Za-z0-9]".toRegex(), "_")
                    val targetName = "avatar_${sanitized}.jpg"
                    val copiedPath = copyUriToInternalStorage(app, uri, targetName)
                    if (copiedPath != null) {
                        // remove previous file if different
                        val accounts = accountRepository.getAllAccountsOnce()
                        val account = accounts.firstOrNull { it.email.equals(email, ignoreCase = true) }
                        account?.avatarPath?.let { prev -> if (prev != copiedPath) try { File(prev).delete() } catch (_: Exception) {} }

                        accountRepository.updateAccountAvatar(email, copiedPath)
                        _uiState.update { it.copy(avatarUri = Uri.fromFile(File(copiedPath))) }
                    } else {
                        // fallback: save legacy uri string
                        avatarRepository.saveLegacyAvatarUri(uri)
                    }
                } else {
                    // No email known yet, save as legacy
                    avatarRepository.saveLegacyAvatarUri(uri)
                }
            }
        }
    }
}
