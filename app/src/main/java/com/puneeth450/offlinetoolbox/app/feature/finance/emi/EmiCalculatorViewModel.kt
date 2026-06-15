package com.puneeth450.offlinetoolbox.app.feature.finance.emi

import androidx.lifecycle.ViewModel
import com.puneeth450.offlinetoolbox.app.domain.finance.EmiResult
import com.puneeth450.offlinetoolbox.app.domain.finance.FinanceCalculators
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

enum class TenureUnit { YEARS, MONTHS }

data class EmiCalculatorUiState(
    val loanAmount: String = "",
    val interestRate: String = "",
    val tenure: String = "",
    val tenureUnit: TenureUnit = TenureUnit.YEARS,
    val result: EmiResult? = null,
    val error: String? = null
) {
    val canCalculate: Boolean
        get() = loanAmount.toDoubleOrNull() != null &&
            interestRate.toDoubleOrNull() != null &&
            tenure.toIntOrNull() != null
}

@HiltViewModel
class EmiCalculatorViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(EmiCalculatorUiState())
    val uiState: StateFlow<EmiCalculatorUiState> = _uiState

    fun onLoanAmountChange(value: String) =
        _uiState.update { it.copy(loanAmount = value, error = null) }

    fun onInterestRateChange(value: String) =
        _uiState.update { it.copy(interestRate = value, error = null) }

    fun onTenureChange(value: String) =
        _uiState.update { it.copy(tenure = value, error = null) }

    fun onTenureUnitChange(unit: TenureUnit) =
        _uiState.update { it.copy(tenureUnit = unit, error = null) }

    fun reset() = _uiState.update { EmiCalculatorUiState() }

    fun calculate() {
        val state = _uiState.value
        runCatching {
            val years = state.tenure.toInt()
            val tenureMonths = when (state.tenureUnit) {
                TenureUnit.YEARS -> years * 12
                TenureUnit.MONTHS -> years
            }
            FinanceCalculators.calculateEmi(
                principal = state.loanAmount.toDouble(),
                annualRate = state.interestRate.toDouble(),
                tenureMonths = tenureMonths
            )
        }.onSuccess { result ->
            _uiState.update { it.copy(result = result, error = null) }
        }.onFailure { error ->
            _uiState.update { it.copy(result = null, error = error.message ?: "Invalid input") }
        }
    }
}
