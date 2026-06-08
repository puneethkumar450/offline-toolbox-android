package com.puneeth450.offlinetoolbox.app.feature.developer.json

import androidx.lifecycle.ViewModel
import com.puneeth450.offlinetoolbox.app.domain.developer.DeveloperTools
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

enum class JsonMode { FORMAT, MINIFY }

data class JsonFormatterUiState(
    val input: String = "",
    val output: String = "",
    val mode: JsonMode = JsonMode.FORMAT,
    val error: String? = null
)

@HiltViewModel
class JsonFormatterViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(JsonFormatterUiState())
    val uiState: StateFlow<JsonFormatterUiState> = _uiState

    fun onInput(value: String) = _uiState.update { it.copy(input = value, error = null) }
    fun setMode(mode: JsonMode) = _uiState.update { it.copy(mode = mode, error = null) }

    fun runPrimary() {
        runCatching {
            when (_uiState.value.mode) {
                JsonMode.FORMAT -> DeveloperTools.formatJson(_uiState.value.input)
                JsonMode.MINIFY -> DeveloperTools.minifyJson(_uiState.value.input)
            }
        }.onSuccess { result ->
            _uiState.update { it.copy(output = result, error = null) }
        }.onFailure { error ->
            _uiState.update { it.copy(error = error.message ?: "Invalid input") }
        }
    }
}
