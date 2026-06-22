package com.puneeth450.offlinetoolbox.app.feature.finance.mutualfund

import androidx.lifecycle.ViewModel
import com.puneeth450.offlinetoolbox.app.domain.finance.FinanceCalculators
import com.puneeth450.offlinetoolbox.app.domain.finance.MutualFundResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

enum class InvestmentType { SIP, LUMPSUM }

data class MutualFundUiState(
    val investmentType: InvestmentType = InvestmentType.SIP,
    val amount: String = "",
    val rate: String = "",
    val period: String = "",
    val result: MutualFundResult? = null,
    val error: String? = null
) {
    val canCalculate: Boolean
        get() = amount.toDoubleOrNull() != null &&
            rate.toDoubleOrNull() != null &&
            period.toIntOrNull() != null
}

@HiltViewModel
class MutualFundViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(MutualFundUiState())
    val uiState: StateFlow<MutualFundUiState> = _uiState

    fun onInvestmentTypeChange(type: InvestmentType) =
        _uiState.update { it.copy(investmentType = type, result = null, error = null) }

    fun onAmountChange(value: String) =
        _uiState.update { it.copy(amount = value, error = null) }

    fun onRateChange(value: String) =
        _uiState.update { it.copy(rate = value, error = null) }

    fun onPeriodChange(value: String) =
        _uiState.update { it.copy(period = value, error = null) }

    fun reset() = _uiState.update { MutualFundUiState() }

    fun calculate() {
        val state = _uiState.value
        runCatching {
            when (state.investmentType) {
                InvestmentType.SIP -> FinanceCalculators.sipReturns(
                    monthlyInvestment = state.amount.toDouble(),
                    annualRate = state.rate.toDouble(),
                    years = state.period.toInt()
                )
                InvestmentType.LUMPSUM -> FinanceCalculators.lumpsumReturns(
                    principal = state.amount.toDouble(),
                    annualRate = state.rate.toDouble(),
                    years = state.period.toInt()
                )
            }
        }.onSuccess { result ->
            _uiState.update { it.copy(result = result, error = null) }
        }.onFailure { error ->
            _uiState.update { it.copy(result = null, error = error.message ?: "Invalid input") }
        }
    }
}
