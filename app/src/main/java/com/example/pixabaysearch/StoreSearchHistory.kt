package com.example.pixabaysearch

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StoreSearchHistory(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "history")
        val HISTORY_KEY = stringPreferencesKey("history")
    }

    val getHistory: Flow<String> = context.dataStore.data.map {
        it[HISTORY_KEY] ?: ""
    }

    suspend fun saveHistory(history: String) {
        context.dataStore.edit {
            it[HISTORY_KEY] = history
        }
    }
}