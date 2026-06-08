package com.puneeth450.offlinetoolbox.app.feature.developer.color

import androidx.lifecycle.ViewModel
import com.puneeth450.offlinetoolbox.app.domain.developer.ColorConverter
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class ColorConverterUiState(val input: String = "", val output: String = "", val error: String? = null)

@HiltViewModel
class ColorConverterViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ColorConverterUiState())
    val uiState: StateFlow<ColorConverterUiState> = _uiState

    fun onInput(value: String) = _uiState.update { it.copy(input = value, error = null) }

    fun runPrimary() {
        runCatching {
            val rgb = ColorConverter.parseHex(_uiState.value.input)
            val hsl = ColorConverter.toHsl(rgb)
            "HEX: ${ColorConverter.toHex(rgb)}\nRGB: ${rgb.r}, ${rgb.g}, ${rgb.b}\nHSL: ${hsl.h}, ${hsl.s}%, ${hsl.l}%"
        }.onSuccess { result ->
            _uiState.update { it.copy(output = result, error = null) }
        }.onFailure { error ->
            _uiState.update { it.copy(error = error.message ?: "Invalid input") }
        }
    }
}
