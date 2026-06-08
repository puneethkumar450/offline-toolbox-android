package com.puneeth450.offlinetoolbox.app.feature.productivity.tally

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class TallyCounterUiState(
    val count: Int = 0
)

@HiltViewModel
class TallyCounterViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(TallyCounterUiState())
    val uiState: StateFlow<TallyCounterUiState> = _uiState

    fun increment() = _uiState.update { it.copy(count = it.count + 1) }
    fun decrement() = _uiState.update { it.copy(count = (it.count - 1).coerceAtLeast(0)) }
    fun reset() = _uiState.update { it.copy(count = 0) }
}
