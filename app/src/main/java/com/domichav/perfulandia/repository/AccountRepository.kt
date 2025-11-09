package com.domichav.perfulandia.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.domichav.perfulandia.data.local.Account
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first

private val Context.accountDataStore by preferencesDataStore(name = "accounts")

class AccountRepository(private val context: Context) {
    companion object {
        private val ACCOUNTS_KEY = stringPreferencesKey("accounts_json")
        private val gson = Gson()
        private val listType = object : TypeToken<List<Account>>() {}.type
    }

    fun getAccountsFlow(): Flow<List<Account>> {
        return context.accountDataStore.data.map { prefs ->
            prefs[ACCOUNTS_KEY]?.let { json ->
                try {
                    gson.fromJson<List<Account>>(json, listType) ?: emptyList()
                } catch (_: Exception) {
                    emptyList()
                }
            } ?: emptyList()
        }
    }

    suspend fun getAllAccountsOnce(): List<Account> {
        val prefs = context.accountDataStore.data.first()
        val json = prefs[ACCOUNTS_KEY] ?: return emptyList()
        return try {
            gson.fromJson(json, listType) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun saveAccount(account: Account) {
        val current = getAllAccountsOnce().toMutableList()
        // Normaliza el email entrante
        val normalizedEmail = account.email.trim().lowercase()
        // Reemplaza el existente por el email normalizado si existe, para evitar duplicados
        val index = current.indexOfFirst { it.email.trim().lowercase() == normalizedEmail }
        if (index >= 0) current[index] = account.copy(email = normalizedEmail) else current.add(account.copy(email = normalizedEmail))
        val json = gson.toJson(current)
        context.accountDataStore.edit { prefs ->
            prefs[ACCOUNTS_KEY] = json
        }
    }

    /**
     * Actualiza solo el avatarPath para la cuenta con el email dado
     */
    suspend fun updateAccountAvatar(email: String, avatarPath: String?) {
        val current = getAllAccountsOnce().toMutableList()
        val normalizedEmail = email.trim().lowercase()
        val index = current.indexOfFirst { it.email.trim().lowercase() == normalizedEmail }
        if (index >= 0) {
            val existing = current[index]
            val updated = existing.copy(avatarPath = avatarPath)
            current[index] = updated
            val json = gson.toJson(current)
            context.accountDataStore.edit { prefs ->
                prefs[ACCOUNTS_KEY] = json
            }
        }
    }

    suspend fun findAccount(email: String, password: String): Account? {
        val normalizedEmail = email.trim().lowercase()
        return getAllAccountsOnce().firstOrNull {
            it.email.trim().lowercase() == normalizedEmail && it.password == password
        }
    }
}