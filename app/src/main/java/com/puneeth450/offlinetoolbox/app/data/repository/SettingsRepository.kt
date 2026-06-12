package com.puneeth450.offlinetoolbox.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

enum class LayoutStyle {
    MODERN,
    CLASSIC
}

class SettingsRepository(private val context: Context) {
    private object Keys {
        val ThemeMode = stringPreferencesKey("theme_mode")
        val LayoutStyle = stringPreferencesKey("layout_style")
        val DynamicColors = stringPreferencesKey("dynamic_colors")
        val Favorites = stringSetPreferencesKey("favorites")
        val RecentTools = stringSetPreferencesKey("recent_tools")
        val CategoryOrder = stringPreferencesKey("category_order")
        val CategoryColors = stringPreferencesKey("category_colors")
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        prefs[Keys.ThemeMode]?.let { value ->
            ThemeMode.entries.firstOrNull { it.name == value }
        } ?: ThemeMode.SYSTEM
    }
    val layoutStyle: Flow<LayoutStyle> = context.dataStore.data.map { prefs ->
        prefs[Keys.LayoutStyle]?.let { value ->
            LayoutStyle.entries.firstOrNull { it.name == value }
        } ?: LayoutStyle.MODERN
    }
    val dynamicColors: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.DynamicColors]?.toBooleanStrictOrNull() ?: true
    }
    val categoryOrder: Flow<List<String>> = context.dataStore.data.map { prefs ->
        prefs[Keys.CategoryOrder]
            ?.split(",")
            ?.map(String::trim)
            ?.filter(String::isNotBlank)
            ?.takeIf { it.isNotEmpty() }
            ?: emptyList()
    }
    val categoryColors: Flow<Map<String, String>> = context.dataStore.data.map { prefs ->
        prefs[Keys.CategoryColors]
            ?.split(";")
            ?.mapNotNull { entry ->
                val parts = entry.split("=")
                if (parts.size == 2) parts[0] to parts[1] else null
            }
            ?.toMap()
            ?: emptyMap()
    }
    val favorites: Flow<Set<String>> = context.dataStore.data.map { it[Keys.Favorites] ?: emptySet() }
    val recentTools: Flow<Set<String>> = context.dataStore.data.map { it[Keys.RecentTools] ?: emptySet() }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.ThemeMode] = mode.name }
    }

    suspend fun setLayoutStyle(style: LayoutStyle) {
        context.dataStore.edit { it[Keys.LayoutStyle] = style.name }
    }

    suspend fun setDynamicColors(enabled: Boolean) {
        context.dataStore.edit { it[Keys.DynamicColors] = enabled.toString() }
    }

    suspend fun setCategoryOrder(order: List<String>) {
        context.dataStore.edit { it[Keys.CategoryOrder] = order.joinToString(",") }
    }

    suspend fun setCategoryColor(categoryName: String, colorHex: String) {
        context.dataStore.edit { prefs ->
            val existing: Map<String, String> = prefs[Keys.CategoryColors]
                ?.split(";")
                ?.mapNotNull { entry ->
                    val parts = entry.split("=")
                    if (parts.size == 2) parts[0] to parts[1] else null
                }
                ?.toMap()
                ?: emptyMap()
            val current = mutableMapOf<String, String>().apply { putAll(existing) }
            current[categoryName] = colorHex
            prefs[Keys.CategoryColors] = current.map { (key, value) -> "$key=$value" }.joinToString(";")
        }
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
