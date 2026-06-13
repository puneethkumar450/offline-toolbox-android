package com.puneeth450.offlinetoolbox.app.feature.productivity.stopwatch

import android.os.SystemClock
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

data class StopwatchLap(
    val index: Int,
    val totalMillis: Long,
    val deltaMillis: Long
)

data class StopwatchTimerUiState(
    val elapsedMillis: Long = 0L,
    val isRunning: Boolean = false,
    val laps: List<StopwatchLap> = emptyList()
)

@HiltViewModel
class StopwatchTimerViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(StopwatchTimerUiState())
    val uiState: StateFlow<StopwatchTimerUiState> = _uiState

    private var ticker: Job? = null
    private var baseElapsedMillis = 0L
    private var startedAtRealtime = 0L

    fun toggle() {
        if (_uiState.value.isRunning) {
            pause()
        } else {
            start()
        }
    }

    fun reset() {
        ticker?.cancel()
        ticker = null
        baseElapsedMillis = 0L
        startedAtRealtime = 0L
        _uiState.value = StopwatchTimerUiState()
    }

    fun recordLap() {
        val currentState = _uiState.value
        if (!currentState.isRunning) return

        val previousLapTotal = currentState.laps.firstOrNull()?.totalMillis ?: 0L
        val lapTotal = currentState.elapsedMillis
        val lapDelta = lapTotal - previousLapTotal
        val nextIndex = currentState.laps.size + 1

        _uiState.update {
            it.copy(
                laps = listOf(
                    StopwatchLap(
                        index = nextIndex,
                        totalMillis = lapTotal,
                        deltaMillis = lapDelta
                    )
                ) + it.laps
            )
        }
    }

    private fun start() {
        if (_uiState.value.isRunning) return
        startedAtRealtime = SystemClock.elapsedRealtime() - baseElapsedMillis
        _uiState.update { it.copy(isRunning = true) }
        ticker?.cancel()
        ticker = viewModelScope.launch {
            while (true) {
                val elapsed = SystemClock.elapsedRealtime() - startedAtRealtime
                baseElapsedMillis = elapsed
                _uiState.update { it.copy(elapsedMillis = elapsed) }
                delay(10L)
            }
        }
    }

    private fun pause() {
        ticker?.cancel()
        ticker = null
        val elapsed = SystemClock.elapsedRealtime() - startedAtRealtime
        baseElapsedMillis = elapsed
        _uiState.update {
            it.copy(
                elapsedMillis = elapsed,
                isRunning = false
            )
        }
    }
}
