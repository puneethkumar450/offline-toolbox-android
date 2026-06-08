package com.puneeth450.offlinetoolbox.app.feature.finance.discount

import androidx.lifecycle.ViewModel
import com.puneeth450.offlinetoolbox.app.domain.finance.FinanceCalculators
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class DiscountCalculatorUiState(
    val input1: String = "",
    val input2: String = "",
    val input3: String = "",
    val result: String = "",
    val error: String? = null
)

@HiltViewModel
class DiscountCalculatorViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(DiscountCalculatorUiState())
    val uiState: StateFlow<DiscountCalculatorUiState> = _uiState

    fun onInput1(value: String) = _uiState.update { it.copy(input1 = value, error = null) }
    fun onInput2(value: String) = _uiState.update { it.copy(input2 = value, error = null) }
    fun onInput3(value: String) = _uiState.update { it.copy(input3 = value, error = null) }

    fun calculate() {
        runCatching {
            when ("discount") {
                "emi" -> {
                    val r = FinanceCalculators.calculateEmi(_uiState.value.input1.toDouble(), _uiState.value.input2.toDouble(), _uiState.value.input3.toInt())
                    "EMI: %.2f
Interest: %.2f
Total: %.2f".format(r.emi, r.totalInterest, r.totalPayment)
                }
                "split" -> {
                    val r = FinanceCalculators.splitBill(_uiState.value.input1.toDouble(), _uiState.value.input2.toInt(), _uiState.value.input3.toDoubleOrNull() ?: 0.0)
                    "Grand total: %.2f
Per person: %.2f".format(r.grandTotal, r.perPerson)
                }
                "discount" -> {
                    val r = FinanceCalculators.discount(_uiState.value.input1.toDouble(), _uiState.value.input2.toDouble())
                    "Final price: %.2f
Saved: %.2f".format(r.finalPrice, r.savedAmount)
                }
                else -> "Years to double: %.2f".format(FinanceCalculators.ruleOf72(_uiState.value.input1.toDouble()))
            }
        }.onSuccess { output -> _uiState.update { it.copy(result = output, error = null) } }
         .onFailure { e -> _uiState.update { it.copy(error = e.message ?: "Invalid input") } }
    }
}
