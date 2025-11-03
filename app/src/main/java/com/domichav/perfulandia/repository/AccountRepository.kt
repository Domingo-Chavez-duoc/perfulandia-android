package com.domichav.perfulandia.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
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
                } catch (e: Exception) {
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
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveAccount(account: Account) {
        val current = getAllAccountsOnce().toMutableList()
        // Replace existing by email if exists, to avoid duplicates
        val index = current.indexOfFirst { it.email.equals(account.email, ignoreCase = true) }
        if (index >= 0) current[index] = account else current.add(account)
        val json = gson.toJson(current)
        context.accountDataStore.edit { prefs ->
            prefs[ACCOUNTS_KEY] = json
        }
    }

    suspend fun findAccount(email: String, password: String): Account? {
        return getAllAccountsOnce().firstOrNull {
            it.email.equals(email, ignoreCase = true) && it.password == password
        }
    }
}