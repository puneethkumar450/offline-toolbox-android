package com.puneeth450.offlinetoolbox.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

class SettingsRepository(private val context: Context) {
    private object Keys {
        val DarkTheme = booleanPreferencesKey("dark_theme")
        val Favorites = stringSetPreferencesKey("favorites")
        val RecentTools = stringSetPreferencesKey("recent_tools")
    }

    val isDarkTheme: Flow<Boolean> = context.dataStore.data.map { it[Keys.DarkTheme] ?: false }
    val favorites: Flow<Set<String>> = context.dataStore.data.map { it[Keys.Favorites] ?: emptySet() }
    val recentTools: Flow<Set<String>> = context.dataStore.data.map { it[Keys.RecentTools] ?: emptySet() }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DarkTheme] = enabled }
    }

    suspend fun toggleFavorite(toolId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.Favorites] ?: emptySet()
            prefs[Keys.Favorites] = if (toolId in current) current - toolId else current + toolId
        }
    }

    suspend fun markRecent(toolId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.RecentTools] ?: emptySet()
            prefs[Keys.RecentTools] = (setOf(toolId) + current).take(10).toSet()
        }
    }
}
