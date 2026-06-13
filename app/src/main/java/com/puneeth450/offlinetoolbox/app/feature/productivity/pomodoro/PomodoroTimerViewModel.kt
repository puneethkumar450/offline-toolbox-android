package com.puneeth450.offlinetoolbox.app.feature.productivity.pomodoro

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

enum class PomodoroPhase { FOCUS, BREAK }

data class PomodoroTimerUiState(
    val focusMinutes: Int = 25,
    val breakMinutes: Int = 5,
    val totalCycles: Int = 4,
    val phase: PomodoroPhase = PomodoroPhase.FOCUS,
    val remainingMillis: Long = 25 * 60 * 1000L,
    val completedSessions: Int = 0,
    val isRunning: Boolean = false
) {
    val currentCycle: Int
        get() = (completedSessions + 1).coerceAtMost(totalCycles)
}

@HiltViewModel
class PomodoroTimerViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(PomodoroTimerUiState())
    val uiState: StateFlow<PomodoroTimerUiState> = _uiState

    private var timerJob: Job? = null

    fun toggleRunning() {
        if (_uiState.value.isRunning) stopTimer() else startTimer()
    }

    fun reset() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                isRunning = false,
                phase = PomodoroPhase.FOCUS,
                remainingMillis = it.focusMinutes * 60 * 1000L,
                completedSessions = 0
            )
        }
    }

    fun saveSettings(focusMinutes: Int, breakMinutes: Int, totalCycles: Int) {
        timerJob?.cancel()
        _uiState.update { state ->
            val safeFocus = focusMinutes.coerceIn(5, 90)
            val safeBreak = breakMinutes.coerceIn(1, 30)
            val safeCycles = totalCycles.coerceIn(1, 8)
            state.copy(
                focusMinutes = safeFocus,
                breakMinutes = safeBreak,
                totalCycles = safeCycles,
                phase = PomodoroPhase.FOCUS,
                remainingMillis = safeFocus * 60 * 1000L,
                completedSessions = 0,
                isRunning = false
            )
        }
    }

    fun skipPhase() {
        timerJob?.cancel()
        transitionToNextPhase()
    }

    private fun startTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isRunning = true) }
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1_000)
                val state = _uiState.value
                if (!state.isRunning) break
                if (state.remainingMillis <= 1_000) {
                    transitionToNextPhase()
                } else {
                    _uiState.update { it.copy(remainingMillis = it.remainingMillis - 1_000) }
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isRunning = false) }
    }

    private fun transitionToNextPhase() {
        _uiState.update { state ->
            val nextPhase = if (state.phase == PomodoroPhase.FOCUS) PomodoroPhase.BREAK else PomodoroPhase.FOCUS
            val nextMillis = if (nextPhase == PomodoroPhase.FOCUS) {
                state.focusMinutes * 60 * 1000L
            } else {
                state.breakMinutes * 60 * 1000L
            }
            state.copy(
                phase = nextPhase,
                remainingMillis = nextMillis,
                completedSessions = if (state.phase == PomodoroPhase.FOCUS) {
                    (state.completedSessions + 1).coerceAtMost(state.totalCycles)
                } else {
                    state.completedSessions
                }
            )
        }
    }
}
