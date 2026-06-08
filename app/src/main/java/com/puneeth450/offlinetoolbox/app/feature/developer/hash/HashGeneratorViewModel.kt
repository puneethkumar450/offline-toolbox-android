package com.puneeth450.offlinetoolbox.app.feature.developer.hash

import androidx.lifecycle.ViewModel
import com.puneeth450.offlinetoolbox.app.domain.developer.DeveloperTools
import com.puneeth450.offlinetoolbox.app.domain.developer.HashAlgorithm
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class HashGeneratorUiState(
    val input: String = "",
    val output: String = "",
    val algorithm: HashAlgorithm = HashAlgorithm.SHA256,
    val error: String? = null
)

@HiltViewModel
class HashGeneratorViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(HashGeneratorUiState())
    val uiState: StateFlow<HashGeneratorUiState> = _uiState

    fun onInput(value: String) = _uiState.update { it.copy(input = value, error = null) }
    fun setAlgorithm(value: HashAlgorithm) = _uiState.update { it.copy(algorithm = value, error = null) }

    fun runPrimary() {
        runCatching { DeveloperTools.hash(_uiState.value.input, _uiState.value.algorithm) }
            .onSuccess { result -> _uiState.update { it.copy(output = result, error = null) } }
            .onFailure { error -> _uiState.update { it.copy(error = error.message ?: "Invalid input") } }
    }
}
