package com.puneeth450.offlinetoolbox.app.feature.productivity.tally

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puneeth450.offlinetoolbox.app.domain.productivity.TimerEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TallyCounterUiState(
    val startedAt: Long = 0L,
    val elapsedMillis: Long = 0L,
    val isRunning: Boolean = false,
    val count: Int = 0
)

@HiltViewModel
class TallyCounterViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(TallyCounterUiState())
    val uiState: StateFlow<TallyCounterUiState> = _uiState
    private var ticker: Job? = null

    fun start() {
        val now = System.currentTimeMillis()
        _uiState.update { it.copy(startedAt = now, isRunning = true) }
        ticker?.cancel()
        ticker = viewModelScope.launch {
            while (true) {
                delay(500)
                _uiState.update { state -> state.copy(elapsedMillis = TimerEngine.elapsedMillis(state.startedAt)) }
            }
        }
    }

    fun stop() { ticker?.cancel(); _uiState.update { it.copy(isRunning = false) } }
    fun reset() { ticker?.cancel(); _uiState.value = TallyCounterUiState() }
    fun increment() = _uiState.update { it.copy(count = it.count + 1) }
    fun decrement() = _uiState.update { it.copy(count = (it.count - 1).coerceAtLeast(0)) }
}
