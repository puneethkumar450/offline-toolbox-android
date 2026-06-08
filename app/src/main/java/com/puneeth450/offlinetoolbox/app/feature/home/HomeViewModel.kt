package com.puneeth450.offlinetoolbox.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneeth450.offlinetoolbox.app.data.repository.SettingsRepository
import com.puneeth450.offlinetoolbox.app.domain.model.ToolCatalog
import com.puneeth450.offlinetoolbox.app.domain.model.ToolCategory
import com.puneeth450.offlinetoolbox.app.domain.model.ToolInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val query: String = "",
    val selectedCategory: ToolCategory? = null,
    val favorites: Set<String> = emptySet(),
    val darkTheme: Boolean = true,
    val tools: List<ToolInfo> = ToolCatalog.all
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val selectedCategory = MutableStateFlow<ToolCategory?>(null)

    val uiState: StateFlow<HomeUiState> = combine(
        query,
        selectedCategory,
        settingsRepository.favorites,
        settingsRepository.isDarkTheme
    ) { search, category, favorites, darkTheme ->
        val filtered = ToolCatalog.all.filter { tool ->
            val matchesQuery = search.isBlank() ||
                tool.title.contains(search, ignoreCase = true) ||
                tool.subtitle.contains(search, ignoreCase = true) ||
                tool.category.title.contains(search, ignoreCase = true) ||
                tool.keywords.any { it.contains(search, ignoreCase = true) }
            val matchesCategory = category == null || tool.category == category
            matchesQuery && matchesCategory
        }
        HomeUiState(search, category, favorites, darkTheme, filtered)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState(darkTheme = true))

    fun onSearchChanged(value: String) { query.value = value }
    fun onCategorySelected(category: ToolCategory?) { selectedCategory.value = category }

    fun toggleFavorite(toolId: String) {
        viewModelScope.launch { settingsRepository.toggleFavorite(toolId) }
    }

    fun toggleTheme() {
        viewModelScope.launch { settingsRepository.setDarkTheme(!uiState.value.darkTheme) }
    }

    fun markRecent(toolId: String) {
        viewModelScope.launch { settingsRepository.markRecent(toolId) }
    }
}
