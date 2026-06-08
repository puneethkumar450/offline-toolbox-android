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
            val result = FinanceCalculators.discount(
                originalPrice = _uiState.value.input1.toDouble(),
                discountPercent = _uiState.value.input2.toDouble()
            )
            "Final price: %.2f\nSaved: %.2f".format(result.finalPrice, result.savedAmount)
        }.onSuccess { output ->
            _uiState.update { it.copy(result = output, error = null) }
        }.onFailure { error ->
            _uiState.update { it.copy(error = error.message ?: "Invalid input") }
        }
    }
}
