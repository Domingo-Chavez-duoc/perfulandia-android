package com.domichav.perfulandia.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property to delegate DataStore creation to the context.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

/**
 * Manages the user's authentication token using Jetpack DataStore.
 */
class SessionManager(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        // The key for storing the auth token in DataStore.
        private val AUTH_TOKEN = stringPreferencesKey("auth_token")
    }

    /**
     * Saves the authentication token to DataStore.
     * @param token The token to save.
     */
    suspend fun saveAuthToken(token: String) {
        dataStore.edit {
            it[AUTH_TOKEN] = token
        }
    }

    /**
     * Retrieves the authentication token from DataStore as a Flow.
     * The Flow will emit the token whenever it changes, or null if it doesn't exist.
     */
    val authToken: Flow<String?> = dataStore.data.map {
        it[AUTH_TOKEN]
    }
}
