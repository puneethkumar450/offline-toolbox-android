package com.puneeth450.offlinetoolbox.app.feature.productivity.stopwatch

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

enum class StopwatchMode { STOPWATCH, TIMER }

data class StopwatchTimerUiState(
    val mode: StopwatchMode = StopwatchMode.STOPWATCH,
    val elapsedMillis: Long = 0L,
    val timerInputMinutes: String = "1",
    val isRunning: Boolean = false
)

@HiltViewModel
class StopwatchTimerViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(StopwatchTimerUiState())
    val uiState: StateFlow<StopwatchTimerUiState> = _uiState

    private var ticker: Job? = null

    fun setMode(mode: StopwatchMode) {
        ticker?.cancel()
        _uiState.update {
            it.copy(
                mode = mode,
                isRunning = false,
                elapsedMillis = if (mode == StopwatchMode.STOPWATCH) 0L else inputMinutesToMillis(it.timerInputMinutes)
            )
        }
    }

    fun onTimerInputChanged(value: String) {
        val sanitized = value.filter { it.isDigit() }.take(3)
        _uiState.update {
            it.copy(
                timerInputMinutes = sanitized.ifBlank { "" },
                elapsedMillis = if (!it.isRunning && it.mode == StopwatchMode.TIMER) {
                    inputMinutesToMillis(sanitized)
                } else {
                    it.elapsedMillis
                }
            )
        }
    }

    fun toggle() {
        if (_uiState.value.isRunning) stop() else start()
    }

    fun reset() {
        ticker?.cancel()
        _uiState.update {
            it.copy(
                isRunning = false,
                elapsedMillis = if (it.mode == StopwatchMode.STOPWATCH) 0L else inputMinutesToMillis(it.timerInputMinutes)
            )
        }
    }

    private fun start() {
        ticker?.cancel()
        _uiState.update { current ->
            current.copy(
                isRunning = true,
                elapsedMillis = if (current.mode == StopwatchMode.TIMER && current.elapsedMillis == 0L) {
                    inputMinutesToMillis(current.timerInputMinutes)
                } else {
                    current.elapsedMillis
                }
            )
        }
        ticker = viewModelScope.launch {
            while (true) {
                delay(100L)
                val current = _uiState.value
                if (!current.isRunning) break
                when (current.mode) {
                    StopwatchMode.STOPWATCH -> _uiState.update { it.copy(elapsedMillis = it.elapsedMillis + 100L) }
                    StopwatchMode.TIMER -> {
                        if (current.elapsedMillis <= 100L) {
                            _uiState.update { it.copy(elapsedMillis = 0L, isRunning = false) }
                            break
                        } else {
                            _uiState.update { it.copy(elapsedMillis = it.elapsedMillis - 100L) }
                        }
                    }
                }
            }
        }
    }

    private fun stop() {
        ticker?.cancel()
        _uiState.update { it.copy(isRunning = false) }
    }

    private fun inputMinutesToMillis(value: String): Long {
        val minutes = value.toLongOrNull()?.coerceIn(0L, 999L) ?: 0L
        return minutes * 60_000L
    }
}
