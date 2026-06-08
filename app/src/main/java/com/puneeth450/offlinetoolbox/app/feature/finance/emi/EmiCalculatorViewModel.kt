package com.puneeth450.offlinetoolbox.app.feature.finance.emi

import androidx.lifecycle.ViewModel
import com.puneeth450.offlinetoolbox.app.domain.finance.FinanceCalculators
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class EmiCalculatorUiState(
    val input1: String = "",
    val input2: String = "",
    val input3: String = "",
    val result: String = "",
    val error: String? = null
)

@HiltViewModel
class EmiCalculatorViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(EmiCalculatorUiState())
    val uiState: StateFlow<EmiCalculatorUiState> = _uiState

    fun onInput1(value: String) = _uiState.update { it.copy(input1 = value, error = null) }
    fun onInput2(value: String) = _uiState.update { it.copy(input2 = value, error = null) }
    fun onInput3(value: String) = _uiState.update { it.copy(input3 = value, error = null) }

    fun calculate() {
        runCatching {
            val result = FinanceCalculators.calculateEmi(
                principal = _uiState.value.input1.toDouble(),
                annualRate = _uiState.value.input2.toDouble(),
                tenureMonths = _uiState.value.input3.toInt() * 12
            )
            "EMI: %.2f\nInterest: %.2f\nTotal: %.2f".format(
                result.emi,
                result.totalInterest,
                result.totalPayment
            )
        }.onSuccess { output ->
            _uiState.update { it.copy(result = output, error = null) }
        }.onFailure { error ->
            _uiState.update { it.copy(error = error.message ?: "Invalid input") }
        }
    }
}
