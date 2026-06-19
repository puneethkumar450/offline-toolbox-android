package com.puneeth450.offlinetoolbox.app.feature.finance.interest

import androidx.lifecycle.ViewModel
import com.puneeth450.offlinetoolbox.app.domain.finance.FinanceCalculators
import com.puneeth450.offlinetoolbox.app.domain.finance.InterestResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

enum class InterestType { SIMPLE, COMPOUND }

enum class CompoundingFrequency(val label: String, val timesPerYear: Int) {
    YEARLY("Yearly (1)", 1),
    HALF_YEARLY("Half-Yearly (2)", 2),
    QUARTERLY("Quarterly (4)", 4),
    MONTHLY("Monthly (12)", 12)
}

data class InterestCalculatorUiState(
    val interestType: InterestType = InterestType.SIMPLE,
    val principal: String = "",
    val rate: String = "",
    val time: String = "",
    val compoundingFrequency: CompoundingFrequency = CompoundingFrequency.YEARLY,
    val result: InterestResult? = null,
    val error: String? = null
) {
    val canCalculate: Boolean
        get() = principal.toDoubleOrNull() != null &&
            rate.toDoubleOrNull() != null &&
            time.toDoubleOrNull() != null
}

@HiltViewModel
class InterestCalculatorViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(InterestCalculatorUiState())
    val uiState: StateFlow<InterestCalculatorUiState> = _uiState

    fun onInterestTypeChange(type: InterestType) =
        _uiState.update { it.copy(interestType = type, error = null) }

    fun onPrincipalChange(value: String) =
        _uiState.update { it.copy(principal = value, error = null) }

    fun onRateChange(value: String) =
        _uiState.update { it.copy(rate = value, error = null) }

    fun onTimeChange(value: String) =
        _uiState.update { it.copy(time = value, error = null) }

    fun onCompoundingFrequencyChange(frequency: CompoundingFrequency) =
        _uiState.update { it.copy(compoundingFrequency = frequency, error = null) }

    fun reset() = _uiState.update { InterestCalculatorUiState() }

    fun calculate() {
        val state = _uiState.value
        runCatching {
            val principal = state.principal.toDouble()
            val rate = state.rate.toDouble()
            val time = state.time.toDouble()
            when (state.interestType) {
                InterestType.SIMPLE -> FinanceCalculators.simpleInterest(principal, rate, time)
                InterestType.COMPOUND -> FinanceCalculators.compoundInterest(
                    principal = principal,
                    annualRate = rate,
                    years = time,
                    timesPerYear = state.compoundingFrequency.timesPerYear
                )
            }
        }.onSuccess { result ->
            _uiState.update { it.copy(result = result, error = null) }
        }.onFailure { error ->
            _uiState.update { it.copy(result = null, error = error.message ?: "Invalid input") }
        }
    }
}
