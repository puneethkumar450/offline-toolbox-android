package com.puneeth450.offlinetoolbox.app.feature.device.flashlight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneeth450.offlinetoolbox.app.data.repository.FlashlightRepository
import com.puneeth450.offlinetoolbox.app.data.repository.FlashlightStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FlashlightUiState(
    val isSupported: Boolean = true,
    val isEnabled: Boolean = false,
    val isAvailable: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class FlashlightViewModel @Inject constructor(
    private val flashlightRepository: FlashlightRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(FlashlightUiState())
    val uiState: StateFlow<FlashlightUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            flashlightRepository.observeTorchState().collect { status ->
                applyStatus(status)
            }
        }
    }

    fun toggleFlashlight() {
        val desired = !_uiState.value.isEnabled
        _uiState.update { it.copy(error = null) }
        flashlightRepository.setTorchEnabled(desired)
            .onFailure { error ->
                _uiState.update { it.copy(error = error.message ?: "Unable to change flashlight state.") }
            }
    }

    private fun applyStatus(status: FlashlightStatus) {
        _uiState.update {
            it.copy(
                isSupported = status.isSupported,
                isEnabled = status.isEnabled,
                isAvailable = status.isAvailable,
                isLoading = false,
                error = status.error
            )
        }
    }
}
