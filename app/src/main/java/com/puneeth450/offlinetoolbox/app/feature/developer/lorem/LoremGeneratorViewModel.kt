package com.puneeth450.offlinetoolbox.app.feature.developer.lorem

import androidx.lifecycle.ViewModel
import com.puneeth450.offlinetoolbox.app.domain.developer.ColorConverter
import com.puneeth450.offlinetoolbox.app.domain.developer.DeveloperTools
import com.puneeth450.offlinetoolbox.app.domain.developer.HashAlgorithm
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
        runCatching {
            when ("lorem") {
                "json" -> DeveloperTools.formatJson(_uiState.value.input)
                "url" -> DeveloperTools.encodeUrl(_uiState.value.input)
                "hash" -> DeveloperTools.hash(_uiState.value.input, HashAlgorithm.SHA256)
                "lorem" -> DeveloperTools.lorem(_uiState.value.input.toIntOrNull() ?: 1)
                else -> {
                    val rgb = ColorConverter.parseHex(_uiState.value.input)
                    val hsl = ColorConverter.toHsl(rgb)
                    "HEX: ${ColorConverter.toHex(rgb)}
RGB: ${rgb.r}, ${rgb.g}, ${rgb.b}
HSL: ${hsl.h}, ${hsl.s}%, ${hsl.l}%"
                }
            }
        }.onSuccess { result -> _uiState.update { it.copy(output = result, error = null) } }
         .onFailure { e -> _uiState.update { it.copy(error = e.message ?: "Invalid input") } }
    }
}
