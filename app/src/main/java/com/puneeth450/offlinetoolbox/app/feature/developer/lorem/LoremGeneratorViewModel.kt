package com.puneeth450.offlinetoolbox.app.feature.developer.lorem

import androidx.lifecycle.ViewModel
import com.puneeth450.offlinetoolbox.app.domain.developer.DeveloperTools
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class LoremGeneratorUiState(val input: String = "", val output: String = "", val error: String? = null)

@HiltViewModel
class LoremGeneratorViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(LoremGeneratorUiState())
    val uiState: StateFlow<LoremGeneratorUiState> = _uiState

    fun onInput(value: String) = _uiState.update { it.copy(input = value, error = null) }

    fun runPrimary() {
        runCatching { DeveloperTools.lorem(_uiState.value.input.toIntOrNull() ?: 1) }
            .onSuccess { result -> _uiState.update { it.copy(output = result, error = null) } }
            .onFailure { error -> _uiState.update { it.copy(error = error.message ?: "Invalid input") } }
    }
}
