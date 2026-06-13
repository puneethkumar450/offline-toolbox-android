package com.puneeth450.offlinetoolbox.app.feature.productivity.tally

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

data class TallyCounterUiState(
    val count: Int = 0,
    val target: Int = 100,
    val autoIntervalMillis: Int = 1000,
    val loopAtTarget: Boolean = false,
    val isAutoRunning: Boolean = false,
    val showAutoStartedMessage: Boolean = false
)

@HiltViewModel
class TallyCounterViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(TallyCounterUiState())
    val uiState: StateFlow<TallyCounterUiState> = _uiState

    private var autoJob: Job? = null

    fun increment() = _uiState.update { current ->
        current.copy(count = nextCount(current, current.count + 1))
    }

    fun decrement() = _uiState.update { current ->
        current.copy(count = (current.count - 1).coerceAtLeast(0))
    }

    fun reset() = _uiState.update {
        it.copy(count = 0, isAutoRunning = false, showAutoStartedMessage = false)
    }.also { stopAutoJob() }

    fun saveTarget(target: Int) {
        val safeTarget = target.coerceAtLeast(1)
        _uiState.update { current ->
            current.copy(
                target = safeTarget,
                count = current.count.coerceAtMost(safeTarget)
            )
        }
    }

    fun saveAutoInterval(intervalMillis: Int) {
        _uiState.update {
            it.copy(autoIntervalMillis = intervalMillis.coerceAtLeast(100))
        }
    }

    fun setLoopAtTarget(enabled: Boolean) {
        _uiState.update { it.copy(loopAtTarget = enabled) }
    }

    fun toggleAuto() {
        if (_uiState.value.isAutoRunning) {
            stopAuto()
        } else {
            startAuto()
        }
    }

    fun dismissAutoStartedMessage() {
        _uiState.update { it.copy(showAutoStartedMessage = false) }
    }

    private fun startAuto() {
        if (_uiState.value.isAutoRunning) return
        _uiState.update { it.copy(isAutoRunning = true, showAutoStartedMessage = true) }
        autoJob?.cancel()
        autoJob = viewModelScope.launch {
            while (true) {
                delay(_uiState.value.autoIntervalMillis.toLong())
                val state = _uiState.value
                if (!state.isAutoRunning) break
                val nextValue = state.count + 1
                if (nextValue > state.target && !state.loopAtTarget) {
                    stopAuto()
                    break
                }
                _uiState.update { current ->
                    current.copy(count = nextCount(current, nextValue))
                }
            }
        }
    }

    private fun stopAuto() {
        stopAutoJob()
        _uiState.update { it.copy(isAutoRunning = false) }
    }

    private fun stopAutoJob() {
        autoJob?.cancel()
        autoJob = null
    }

    private fun nextCount(state: TallyCounterUiState, rawValue: Int): Int {
        return when {
            rawValue <= state.target -> rawValue
            state.loopAtTarget -> 0
            else -> state.target
        }
    }

    override fun onCleared() {
        stopAutoJob()
        super.onCleared()
    }
}
