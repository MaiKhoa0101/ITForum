package com.example.itforum.user.utilities.search

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "search_history")

object SearchHistoryManager {
    private const val MAX_HISTORY = 50

    // Tạo key riêng cho từng user
    private fun getKeyForUser(userId: String): Preferences.Key<Set<String>> {
        return stringSetPreferencesKey("search_history_$userId")
    }

    suspend fun addSearchQuery(context: Context, userId: String, query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return

        val key = getKeyForUser(userId)

        context.dataStore.edit { prefs ->
            val current = prefs[key]?.toMutableSet() ?: mutableSetOf()
            current.remove(trimmed) // nếu có rồi, đưa lên đầu
            current.add(trimmed)

            // Giới hạn độ dài
            val newSet = current.toList().takeLast(MAX_HISTORY).toSet()

            prefs[key] = newSet
        }
    }

    suspend fun getSearchHistory(context: Context, userId: String): List<String> {
        val key = getKeyForUser(userId)
        return context.dataStore.data
            .map { prefs -> prefs[key]?.toList() ?: emptyList() }
            .first()
            .reversed() // Mới nhất lên đầu
    }

    suspend fun clearHistory(context: Context, userId: String) {
        val key = getKeyForUser(userId)
        context.dataStore.edit { prefs ->
            prefs.remove(key)
        }
    }

    suspend fun removeSearchQuery(context: Context, userId: String, query: String) {
        val key = getKeyForUser(userId)
        context.dataStore.edit { prefs ->
            val current = prefs[key]?.toMutableSet() ?: return@edit
            current.remove(query)
            prefs[key] = current
        }
    }
}
