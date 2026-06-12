package com.puneeth450.offlinetoolbox.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneeth450.offlinetoolbox.app.data.repository.LayoutStyle
import com.puneeth450.offlinetoolbox.app.data.repository.SettingsRepository
import com.puneeth450.offlinetoolbox.app.data.repository.ThemeMode
import com.puneeth450.offlinetoolbox.app.domain.model.ToolCatalog
import com.puneeth450.offlinetoolbox.app.domain.model.ToolCategory
import com.puneeth450.offlinetoolbox.app.domain.model.ToolInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class HomeLayoutMode { LIST, GRID }

data class HomeUiState(
    val query: String = "",
    val selectedCategory: ToolCategory? = null,
    val favorites: Set<String> = emptySet(),
    val recentTools: List<ToolInfo> = emptyList(),
    val isHistoryVisible: Boolean = false,
    val layoutMode: HomeLayoutMode = HomeLayoutMode.LIST,
    val darkTheme: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColors: Boolean = true,
    val orderedCategories: List<ToolCategory> = ToolCategory.entries,
    val categoryColors: Map<ToolCategory, String> = ToolCategory.entries.associateWith { it.defaultColorHex },
    val tools: List<ToolInfo> = ToolCatalog.all
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val selectedCategory = MutableStateFlow<ToolCategory?>(null)
    private val isHistoryVisible = MutableStateFlow(false)
    private val homePrefs = combine(
        query,
        selectedCategory,
        isHistoryVisible
    ) { search, category, historyVisible ->
        HomeUiState(
            query = search,
            selectedCategory = category,
            isHistoryVisible = historyVisible
        )
    }
    private val catalogState = homePrefs.map { base ->
        val filtered = ToolCatalog.all.filter { tool ->
            val matchesQuery = base.query.isBlank() ||
                tool.title.contains(base.query, ignoreCase = true) ||
                tool.subtitle.contains(base.query, ignoreCase = true) ||
                tool.category.title.contains(base.query, ignoreCase = true) ||
                tool.keywords.any { it.contains(base.query, ignoreCase = true) }
            val matchesCategory = base.selectedCategory == null || tool.category == base.selectedCategory
            matchesQuery && matchesCategory
        }
        base.copy(tools = filtered)
    }
    private val categorySettingsState = combine(
        settingsRepository.categoryOrder,
        settingsRepository.categoryColors
    ) { categoryOrder, categoryColors ->
        CategorySettingsUiState(categoryOrder, categoryColors)
    }
    private val baseSettingsState = combine(
        settingsRepository.favorites,
        settingsRepository.recentTools,
        settingsRepository.themeMode,
        settingsRepository.dynamicColors,
        settingsRepository.layoutStyle
    ) { favorites, recentIds, themeMode, dynamicColors, layoutStyle ->
        BaseSettingsUiState(favorites, recentIds, themeMode, dynamicColors, layoutStyle)
    }
    private val settingsState = combine(
        baseSettingsState,
        categorySettingsState
    ) { base, categories ->
        SettingsUiState(
            base.favorites,
            base.recentIds,
            base.themeMode,
            base.dynamicColors,
            base.layoutStyle,
            categories.categoryOrder,
            categories.categoryColors
        )
    }

    val uiState: StateFlow<HomeUiState> = combine(
        catalogState,
        settingsState
    ) { catalog, settings ->
        val recentTools = settings.recentIds.mapNotNull { id -> ToolCatalog.all.find { it.id == id } }
        val orderedCategories = buildList {
            settings.categoryOrder.forEach { name ->
                ToolCategory.entries.firstOrNull { it.name == name }?.let(::add)
            }
            ToolCategory.entries.filterNot { it in this }.forEach(::add)
        }
        val resolvedColors = ToolCategory.entries.associateWith { category ->
            settings.categoryColors[category.name] ?: category.defaultColorHex
        }
        catalog.copy(
            favorites = settings.favorites,
            recentTools = recentTools,
            darkTheme = settings.themeMode == ThemeMode.DARK,
            themeMode = settings.themeMode,
            dynamicColors = settings.dynamicColors,
            layoutMode = settings.layoutStyle.toHomeLayoutMode(),
            orderedCategories = orderedCategories,
            categoryColors = resolvedColors
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState(darkTheme = true))

    fun onSearchChanged(value: String) { query.value = value }
    fun onCategorySelected(category: ToolCategory?) { selectedCategory.value = category }
    fun toggleHistory() { isHistoryVisible.value = !isHistoryVisible.value }
    fun toggleLayoutMode() {
        val next = if (uiState.value.layoutMode == HomeLayoutMode.LIST) LayoutStyle.CLASSIC else LayoutStyle.MODERN
        setLayoutStyle(next)
    }

    fun toggleFavorite(toolId: String) {
        viewModelScope.launch { settingsRepository.toggleFavorite(toolId) }
    }

    fun cycleThemeMode() {
        val next = when (uiState.value.themeMode) {
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.SYSTEM
            ThemeMode.SYSTEM -> ThemeMode.LIGHT
        }
        setThemeMode(next)
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode(mode) }
    }

    fun setLayoutStyle(style: LayoutStyle) {
        viewModelScope.launch { settingsRepository.setLayoutStyle(style) }
    }

    fun setDynamicColors(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setDynamicColors(enabled) }
    }

    fun setCategoryColor(category: ToolCategory, colorHex: String) {
        viewModelScope.launch { settingsRepository.setCategoryColor(category.name, colorHex) }
    }

    fun setCategoryOrder(order: List<ToolCategory>) {
        viewModelScope.launch { settingsRepository.setCategoryOrder(order.map { it.name }) }
    }

    fun moveCategory(category: ToolCategory, direction: Int) {
        val current = uiState.value.orderedCategories.toMutableList()
        val index = current.indexOf(category)
        if (index == -1) return
        val newIndex = (index + direction).coerceIn(0, current.lastIndex)
        if (newIndex == index) return
        current.removeAt(index)
        current.add(newIndex, category)
        viewModelScope.launch { settingsRepository.setCategoryOrder(current.map { it.name }) }
    }

    fun markRecent(toolId: String) {
        viewModelScope.launch { settingsRepository.markRecent(toolId) }
    }
}

private data class SettingsUiState(
    val favorites: Set<String>,
    val recentIds: Set<String>,
    val themeMode: ThemeMode,
    val dynamicColors: Boolean,
    val layoutStyle: LayoutStyle,
    val categoryOrder: List<String>,
    val categoryColors: Map<String, String>
)

private data class BaseSettingsUiState(
    val favorites: Set<String>,
    val recentIds: Set<String>,
    val themeMode: ThemeMode,
    val dynamicColors: Boolean,
    val layoutStyle: LayoutStyle
)

private data class CategorySettingsUiState(
    val categoryOrder: List<String>,
    val categoryColors: Map<String, String>
)

private fun LayoutStyle.toHomeLayoutMode(): HomeLayoutMode = when (this) {
    LayoutStyle.MODERN -> HomeLayoutMode.LIST
    LayoutStyle.CLASSIC -> HomeLayoutMode.GRID
}
