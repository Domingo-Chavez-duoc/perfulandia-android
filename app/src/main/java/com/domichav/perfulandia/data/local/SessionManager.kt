package com.domichav.perfulandia.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/// Propiedad de extensión para delegar la creación de DataStore al contexto
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

/**
 * Maneja el token de autenticación del usuario utilizando Jetpack DataStore
 */
class SessionManager(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        // Llave para almacenar el token de autenticación en DataStore.
        private val AUTH_TOKEN = stringPreferencesKey("auth_token")
    }

    /**
     * Guarda el token de autenticación en DataStore
     * @param token el token de autenticación a guardar
     *
     */
    suspend fun saveAuthToken(token: String) {
        dataStore.edit {
            it[AUTH_TOKEN] = token
        }
    }

    /**
     * *Recupera el token de autenticación de DataStore como un flujo
     * EL flujo emitirá el token cada vez que cambie, o null si no existe
     */
    val authToken: Flow<String?> = dataStore.data.map {
        it[AUTH_TOKEN]
    }
}
