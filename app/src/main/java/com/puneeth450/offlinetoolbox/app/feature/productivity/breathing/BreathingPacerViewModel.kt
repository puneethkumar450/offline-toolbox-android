package com.puneeth450.offlinetoolbox.app.feature.productivity.breathing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class BreathingPhase(val label: String, val seconds: Int) {
    INHALE("Inhale", 4),
    HOLD("Hold", 4),
    EXHALE("Exhale", 6)
}

data class BreathingPacerUiState(
    val phase: BreathingPhase = BreathingPhase.INHALE,
    val phaseRemainingSeconds: Int = BreathingPhase.INHALE.seconds,
    val cyclesCompleted: Int = 0,
    val isRunning: Boolean = false
)

@HiltViewModel
class BreathingPacerViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(BreathingPacerUiState())
    val uiState: StateFlow<BreathingPacerUiState> = _uiState

    private var ticker: Job? = null

    fun toggle() {
        if (_uiState.value.isRunning) pause() else start()
    }

    fun reset() {
        ticker?.cancel()
        _uiState.value = BreathingPacerUiState()
    }

    private fun start() {
        ticker?.cancel()
        _uiState.update { it.copy(isRunning = true) }
        ticker = viewModelScope.launch {
            while (true) {
                delay(1_000)
                val current = _uiState.value
                if (!current.isRunning) break
                if (current.phaseRemainingSeconds <= 1) {
                    val nextPhase = when (current.phase) {
                        BreathingPhase.INHALE -> BreathingPhase.HOLD
                        BreathingPhase.HOLD -> BreathingPhase.EXHALE
                        BreathingPhase.EXHALE -> BreathingPhase.INHALE
                    }
                    _uiState.update {
                        it.copy(
                            phase = nextPhase,
                            phaseRemainingSeconds = nextPhase.seconds,
                            cyclesCompleted = if (current.phase == BreathingPhase.EXHALE) current.cyclesCompleted + 1 else current.cyclesCompleted
                        )
                    }
                } else {
                    _uiState.update { it.copy(phaseRemainingSeconds = it.phaseRemainingSeconds - 1) }
                }
            }
        }
    }

    private fun pause() {
        ticker?.cancel()
        _uiState.update { it.copy(isRunning = false) }
    }
}
