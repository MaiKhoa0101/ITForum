package com.example.itforum.user.utilities.search


import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "search_history")

object SearchHistoryManager {
    private val SEARCH_HISTORY_KEY = stringSetPreferencesKey("search_history")
    private const val KEY_HISTORY = "search_list"
    private const val MAX_HISTORY = 50
    private const val PREF_NAME = "search_history"
    suspend fun addSearchQuery(context: Context, query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return

        context.dataStore.edit { prefs ->
            val current = prefs[SEARCH_HISTORY_KEY]?.toMutableSet() ?: mutableSetOf()
            current.add(trimmed)
            prefs[SEARCH_HISTORY_KEY] = current
        }
    }

    suspend fun getSearchHistory(context: Context): List<String> {
        return context.dataStore.data
            .map { prefs -> prefs[SEARCH_HISTORY_KEY]?.toList() ?: emptyList() }
            .first()
            .sortedByDescending { it } // optional: you can remove or modify
    }
    suspend fun clearHistory(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.remove(SEARCH_HISTORY_KEY)
        }
    }

    suspend fun removeSearchQuery(context: Context, query: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[SEARCH_HISTORY_KEY]?.toMutableSet() ?: return@edit
            current.remove(query)
            prefs[SEARCH_HISTORY_KEY] = current
        }


    }}
