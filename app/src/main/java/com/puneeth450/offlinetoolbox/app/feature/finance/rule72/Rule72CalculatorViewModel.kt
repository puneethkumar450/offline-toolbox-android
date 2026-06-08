package com.puneeth450.offlinetoolbox.app.feature.finance.rule72

import androidx.lifecycle.ViewModel
import com.puneeth450.offlinetoolbox.app.domain.finance.FinanceCalculators
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class Rule72CalculatorUiState(
    val input1: String = "",
    val input2: String = "",
    val input3: String = "",
    val result: String = "",
    val error: String? = null
)

@HiltViewModel
class Rule72CalculatorViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(Rule72CalculatorUiState())
    val uiState: StateFlow<Rule72CalculatorUiState> = _uiState

    fun onInput1(value: String) = _uiState.update { it.copy(input1 = value, error = null) }
    fun onInput2(value: String) = _uiState.update { it.copy(input2 = value, error = null) }
    fun onInput3(value: String) = _uiState.update { it.copy(input3 = value, error = null) }

    fun calculate() {
        runCatching {
            val years = FinanceCalculators.ruleOf72(_uiState.value.input1.toDouble())
            "Years to double: %.2f".format(years)
        }.onSuccess { output ->
            _uiState.update { it.copy(result = output, error = null) }
        }.onFailure { error ->
            _uiState.update { it.copy(error = error.message ?: "Invalid input") }
        }
    }
}
