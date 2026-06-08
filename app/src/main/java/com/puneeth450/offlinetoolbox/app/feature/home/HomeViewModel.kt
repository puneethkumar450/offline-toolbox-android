package com.puneeth450.offlinetoolbox.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneeth450.offlinetoolbox.app.data.repository.SettingsRepository
import com.puneeth450.offlinetoolbox.app.domain.model.ToolCatalog
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
    val favorites: Set<String> = emptySet(),
    val tools: List<ToolInfo> = ToolCatalog.all
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val query = MutableStateFlow("")

    val uiState: StateFlow<HomeUiState> = combine(
        query,
        settingsRepository.favorites
    ) { search, favorites ->
        val filtered = ToolCatalog.all.filter { tool ->
            tool.title.contains(search, ignoreCase = true) ||
                tool.category.title.contains(search, ignoreCase = true)
        }
        HomeUiState(search, favorites, filtered)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun onSearchChanged(value: String) { query.value = value }

    fun toggleFavorite(toolId: String) {
        viewModelScope.launch { settingsRepository.toggleFavorite(toolId) }
    }

    fun markRecent(toolId: String) {
        viewModelScope.launch { settingsRepository.markRecent(toolId) }
    }
}
