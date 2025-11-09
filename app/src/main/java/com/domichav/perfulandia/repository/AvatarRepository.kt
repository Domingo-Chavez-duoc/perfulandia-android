package com.domichav.perfulandia.repository

import android.content.Context
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensión para crear el DataStore de avatar
private val Context.avatarDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "avatar_preferences"
)

class AvatarRepository(private val context: Context) {

    companion object {
        // Llave única heredada mantenida para compatibilidad si es necesario
        private const val LEGACY_KEY = "avatar_uri_key"
    }

    private fun keyForEmail(email: String): androidx.datastore.preferences.core.Preferences.Key<String> {
        // Normaliza el email para usarlo como llave
        val safeKey = "avatar_uri_${email.trim().lowercase()}"
        return stringPreferencesKey(safeKey)
    }

    /**
     * Obtiene el URI del avatar para un email específico como Flow reactivo
     */
    fun getAvatarUriFor(email: String): Flow<Uri?> {
        val key = keyForEmail(email)
        return context.avatarDataStore.data.map { preferences ->
            preferences[key]?.let { uriString ->
                Uri.parse(uriString)
            }
        }
    }

    /**
     * Guarda el URI del avatar para un email específico en DataStore
     */
    suspend fun saveAvatarUriFor(email: String, uri: Uri?) {
        val key = keyForEmail(email)
        if (uri != null) {
            context.avatarDataStore.edit { preferences ->
                preferences[key] = uri.toString()
            }
        } else {
            context.avatarDataStore.edit { preferences ->
                preferences.remove(key)
            }
        }
    }

    /**
     * Elimina el URI del avatar para un email específico
     */
    suspend fun clearAvatarUriFor(email: String) {
        val key = keyForEmail(email)
        context.avatarDataStore.edit { preferences ->
            preferences.remove(key)
        }
    }

    // Ayuda de compatibilidad hacia atrás: devuelve el avatar único heredado (no por cuenta)
    fun getLegacyAvatarUri(): Flow<Uri?> {
        val key = stringPreferencesKey(LEGACY_KEY)
        return context.avatarDataStore.data.map { preferences ->
            preferences[key]?.let { Uri.parse(it) }
        }
    }

    //Ayuda de compatibilidad hacia atrás para guardar la llave de avatar heredada
    suspend fun saveLegacyAvatarUri(uri: Uri?) {
        val key = stringPreferencesKey(LEGACY_KEY)
        if (uri != null) {
            context.avatarDataStore.edit { preferences ->
                preferences[key] = uri.toString()
            }
        } else {
            context.avatarDataStore.edit { preferences ->
                preferences.remove(key)
            }
        }
    }
}