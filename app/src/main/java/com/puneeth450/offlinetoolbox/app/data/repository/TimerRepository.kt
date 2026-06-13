package com.puneeth450.offlinetoolbox.app.data.repository

import android.os.SystemClock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TimerUiState(
    val hours: Int = 0,
    val minutes: Int = 5,
    val seconds: Int = 0,
    val totalDurationMillis: Long = 5 * 60_000L,
    val remainingMillis: Long = 5 * 60_000L,
    val isRunning: Boolean = false,
    val completionCount: Int = 0
)

@Singleton
class TimerRepository @Inject constructor() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _uiState = MutableStateFlow(TimerUiState())
    val uiState: StateFlow<TimerUiState> = _uiState

    private var tickerJob: Job? = null
    private var finishAtRealtime: Long = 0L

    fun setHours(value: Int) = updateSelection(hours = value.coerceIn(0, 23))
    fun setMinutes(value: Int) = updateSelection(minutes = value.coerceIn(0, 59))
    fun setSeconds(value: Int) = updateSelection(seconds = value.coerceIn(0, 59))

    fun applyPreset(totalMinutes: Int) {
        val safeMinutes = totalMinutes.coerceAtLeast(0)
        updateSelection(
            hours = safeMinutes / 60,
            minutes = safeMinutes % 60,
            seconds = 0
        )
    }

    fun start() {
        val current = _uiState.value
        if (current.isRunning || current.remainingMillis <= 0L) return

        finishAtRealtime = SystemClock.elapsedRealtime() + current.remainingMillis
        _uiState.update { it.copy(isRunning = true) }
        tickerJob?.cancel()
        tickerJob = scope.launch {
            while (true) {
                val remaining = max(0L, finishAtRealtime - SystemClock.elapsedRealtime())
                if (remaining == 0L) {
                    _uiState.update {
                        it.copy(
                            remainingMillis = 0L,
                            isRunning = false,
                            completionCount = it.completionCount + 1
                        )
                    }
                    break
                } else {
                    _uiState.update { it.copy(remainingMillis = remaining) }
                }
                delay(250L)
            }
        }
    }

    fun pause() {
        if (!_uiState.value.isRunning) return
        tickerJob?.cancel()
        tickerJob = null
        _uiState.update {
            it.copy(
                isRunning = false,
                remainingMillis = max(0L, finishAtRealtime - SystemClock.elapsedRealtime())
            )
        }
    }

    fun reset() {
        tickerJob?.cancel()
        tickerJob = null
        _uiState.update {
            it.copy(
                isRunning = false,
                remainingMillis = it.totalDurationMillis
            )
        }
    }

    private fun updateSelection(hours: Int? = null, minutes: Int? = null, seconds: Int? = null) {
        if (_uiState.value.isRunning) return
        _uiState.update { current ->
            val updatedHours = hours ?: current.hours
            val updatedMinutes = minutes ?: current.minutes
            val updatedSeconds = seconds ?: current.seconds
            val durationMillis = ((updatedHours * 3600) + (updatedMinutes * 60) + updatedSeconds) * 1_000L
            current.copy(
                hours = updatedHours,
                minutes = updatedMinutes,
                seconds = updatedSeconds,
                totalDurationMillis = durationMillis,
                remainingMillis = durationMillis,
                isRunning = false
            )
        }
    }
}
