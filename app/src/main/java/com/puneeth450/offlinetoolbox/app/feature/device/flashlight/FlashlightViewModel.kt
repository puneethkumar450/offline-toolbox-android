package com.puneeth450.offlinetoolbox.app.feature.device.flashlight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneeth450.offlinetoolbox.app.data.repository.FlashlightRepository
import com.puneeth450.offlinetoolbox.app.data.repository.FlashlightStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class FlashMode {
    Rear,
    Screen
}

data class FlashlightUiState(
    val isSupported: Boolean = true,
    val isEnabled: Boolean = false,
    val isAvailable: Boolean = false,
    val isLoading: Boolean = true,
    val flashMode: FlashMode = FlashMode.Rear,
    val screenLightOn: Boolean = false,
    val rearStrobeOn: Boolean = false,
    val screenStrobeOn: Boolean = false,
    val strobeSpeedHz: Int = 5,
    val error: String? = null
)

@HiltViewModel
class FlashlightViewModel @Inject constructor(
    private val flashlightRepository: FlashlightRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(FlashlightUiState())
    val uiState: StateFlow<FlashlightUiState> = _uiState.asStateFlow()
    private var rearStrobeJob: Job? = null
    private var screenStrobeJob: Job? = null

    init {
        viewModelScope.launch {
            flashlightRepository.observeTorchState().collect { status ->
                applyStatus(status)
            }
        }
    }

    fun toggleFlashlight() {
        when (_uiState.value.flashMode) {
            FlashMode.Rear -> toggleRearFlash()
            FlashMode.Screen -> toggleScreenFlash()
        }
    }

    fun setFlashMode(mode: FlashMode) {
        if (_uiState.value.flashMode == mode) return
        stopStrobes()
        if (_uiState.value.isEnabled) {
            flashlightRepository.setTorchEnabled(false)
        }
        _uiState.update {
            it.copy(
                flashMode = mode,
                screenLightOn = false,
                error = null
            )
        }
    }

    fun setStrobeSpeed(speedHz: Int) {
        val boundedSpeed = speedHz.coerceIn(MinStrobeHz, MaxStrobeHz)
        _uiState.update { it.copy(strobeSpeedHz = boundedSpeed) }
        if (_uiState.value.rearStrobeOn) {
            startRearStrobe()
        }
        if (_uiState.value.screenStrobeOn) {
            startScreenStrobe()
        }
    }

    fun toggleRearStrobe() {
        if (_uiState.value.rearStrobeOn) {
            stopRearStrobe()
        } else {
            _uiState.update {
                it.copy(
                    flashMode = FlashMode.Rear,
                    screenLightOn = false,
                    screenStrobeOn = false,
                    rearStrobeOn = true,
                    error = null
                )
            }
            screenStrobeJob?.cancel()
            startRearStrobe()
        }
    }

    fun toggleScreenStrobe() {
        if (_uiState.value.screenStrobeOn) {
            stopScreenStrobe()
        } else {
            if (_uiState.value.isEnabled) {
                flashlightRepository.setTorchEnabled(false)
            }
            _uiState.update {
                it.copy(
                    flashMode = FlashMode.Screen,
                    rearStrobeOn = false,
                    screenStrobeOn = true,
                    screenLightOn = true,
                    error = null
                )
            }
            rearStrobeJob?.cancel()
            startScreenStrobe()
        }
    }

    private fun toggleRearFlash() {
        if (_uiState.value.rearStrobeOn) {
            stopRearStrobe()
            return
        }
        stopStrobes()
        val desired = !_uiState.value.isEnabled
        _uiState.update { it.copy(error = null) }
        flashlightRepository.setTorchEnabled(desired)
            .onFailure { error ->
                _uiState.update { it.copy(error = error.message ?: "Unable to change flashlight state.") }
            }
    }

    private fun toggleScreenFlash() {
        if (_uiState.value.screenStrobeOn) {
            stopScreenStrobe()
            return
        }
        stopStrobes()
        _uiState.update {
            it.copy(
                screenLightOn = !it.screenLightOn,
                error = null
            )
        }
    }

    private fun startRearStrobe() {
        rearStrobeJob?.cancel()
        rearStrobeJob = viewModelScope.launch {
            while (isActive && _uiState.value.rearStrobeOn) {
                val delayMillis = strobeDelayMillis()
                flashlightRepository.setTorchEnabled(true)
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(
                                rearStrobeOn = false,
                                error = error.message ?: "Unable to change flashlight state."
                            )
                        }
                        return@launch
                    }
                delay(delayMillis)
                flashlightRepository.setTorchEnabled(false)
                delay(delayMillis)
            }
        }
    }

    private fun startScreenStrobe() {
        screenStrobeJob?.cancel()
        screenStrobeJob = viewModelScope.launch {
            while (isActive && _uiState.value.screenStrobeOn) {
                delay(strobeDelayMillis())
                _uiState.update { it.copy(screenLightOn = !it.screenLightOn) }
            }
        }
    }

    private fun stopStrobes() {
        stopRearStrobe()
        stopScreenStrobe()
    }

    private fun stopRearStrobe() {
        rearStrobeJob?.cancel()
        rearStrobeJob = null
        flashlightRepository.setTorchEnabled(false)
        _uiState.update { it.copy(rearStrobeOn = false) }
    }

    private fun stopScreenStrobe() {
        screenStrobeJob?.cancel()
        screenStrobeJob = null
        _uiState.update { it.copy(screenStrobeOn = false, screenLightOn = false) }
    }

    private fun strobeDelayMillis(): Long {
        return (1000L / (_uiState.value.strobeSpeedHz.coerceIn(MinStrobeHz, MaxStrobeHz) * 2)).coerceAtLeast(40L)
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

    override fun onCleared() {
        stopStrobes()
        flashlightRepository.setTorchEnabled(false)
        super.onCleared()
    }

    companion object {
        const val MinStrobeHz = 1
        const val MaxStrobeHz = 10
    }
}
