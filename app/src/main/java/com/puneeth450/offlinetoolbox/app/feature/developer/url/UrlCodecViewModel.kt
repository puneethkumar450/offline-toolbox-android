package com.puneeth450.offlinetoolbox.app.feature.developer.url

import androidx.lifecycle.ViewModel
import com.puneeth450.offlinetoolbox.app.domain.developer.DeveloperTools
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

enum class UrlCodecMode { ENCODE, DECODE }

data class UrlCodecUiState(
    val input: String = "",
    val output: String = "",
    val mode: UrlCodecMode = UrlCodecMode.ENCODE,
    val error: String? = null
)

@HiltViewModel
class UrlCodecViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(UrlCodecUiState())
    val uiState: StateFlow<UrlCodecUiState> = _uiState

    fun onInput(value: String) = _uiState.update { it.copy(input = value, error = null) }
    fun setMode(mode: UrlCodecMode) = _uiState.update { it.copy(mode = mode, error = null) }

    fun runPrimary() {
        runCatching {
            when (_uiState.value.mode) {
                UrlCodecMode.ENCODE -> DeveloperTools.encodeUrl(_uiState.value.input)
                UrlCodecMode.DECODE -> DeveloperTools.decodeUrl(_uiState.value.input)
            }
        }.onSuccess { result ->
            _uiState.update { it.copy(output = result, error = null) }
        }.onFailure { error ->
            _uiState.update { it.copy(error = error.message ?: "Invalid input") }
        }
    }
}
