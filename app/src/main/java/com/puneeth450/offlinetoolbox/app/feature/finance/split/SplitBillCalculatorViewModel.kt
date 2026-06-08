package com.puneeth450.offlinetoolbox.app.feature.finance.split

import androidx.lifecycle.ViewModel
import com.puneeth450.offlinetoolbox.app.domain.finance.FinanceCalculators
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class SplitBillCalculatorUiState(
    val input1: String = "",
    val input2: String = "",
    val input3: String = "",
    val result: String = "",
    val error: String? = null
)

@HiltViewModel
class SplitBillCalculatorViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(SplitBillCalculatorUiState())
    val uiState: StateFlow<SplitBillCalculatorUiState> = _uiState

    fun onInput1(value: String) = _uiState.update { it.copy(input1 = value, error = null) }
    fun onInput2(value: String) = _uiState.update { it.copy(input2 = value, error = null) }
    fun onInput3(value: String) = _uiState.update { it.copy(input3 = value, error = null) }

    fun calculate() {
        runCatching {
            val result = FinanceCalculators.splitBill(
                total = _uiState.value.input1.toDouble(),
                people = _uiState.value.input2.toInt(),
                tipPercent = _uiState.value.input3.toDoubleOrNull() ?: 0.0
            )
            "Grand total: %.2f\nPer person: %.2f".format(result.grandTotal, result.perPerson)
        }.onSuccess { output ->
            _uiState.update { it.copy(result = output, error = null) }
        }.onFailure { error ->
            _uiState.update { it.copy(error = error.message ?: "Invalid input") }
        }
    }
}
