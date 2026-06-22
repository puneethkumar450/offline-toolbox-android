package com.puneeth450.offlinetoolbox.app.feature.finance.fdrd

import androidx.lifecycle.ViewModel
import com.puneeth450.offlinetoolbox.app.domain.finance.FdRdResult
import com.puneeth450.offlinetoolbox.app.domain.finance.FinanceCalculators
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

enum class DepositType { FD, RD }
enum class TenureUnit { YEARS, MONTHS }
enum class CompoundingFreq(val label: String, val timesPerYear: Int) {
    QUARTERLY("Quarterly", 4),
    YEARLY("Yearly", 1)
}

data class FdRdUiState(
    val depositType: DepositType = DepositType.FD,
    val amount: String = "",           // principal (FD) or monthly deposit (RD)
    val rate: String = "",
    val tenure: String = "",
    val tenureUnit: TenureUnit = TenureUnit.YEARS,
    val compoundingFreq: CompoundingFreq = CompoundingFreq.QUARTERLY,
    val result: FdRdResult = FdRdResult(0.0, 0.0, 0.0, 0.0)
)

@HiltViewModel
class FdRdViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(FdRdUiState())
    val uiState: StateFlow<FdRdUiState> = _uiState

    fun onDepositTypeChange(type: DepositType) {
        _uiState.update { it.copy(depositType = type, result = FdRdResult(0.0, 0.0, 0.0, 0.0)) }
        recalculate()
    }

    fun onAmountChange(v: String) { _uiState.update { it.copy(amount = v) }; recalculate() }
    fun onRateChange(v: String) { _uiState.update { it.copy(rate = v) }; recalculate() }
    fun onTenureChange(v: String) { _uiState.update { it.copy(tenure = v) }; recalculate() }
    fun onTenureUnitChange(u: TenureUnit) { _uiState.update { it.copy(tenureUnit = u) }; recalculate() }
    fun onCompoundingFreqChange(f: CompoundingFreq) { _uiState.update { it.copy(compoundingFreq = f) }; recalculate() }
    fun reset() { _uiState.update { FdRdUiState() } }

    private fun recalculate() {
        val s = _uiState.value
        val amount = s.amount.toDoubleOrNull() ?: return
        val rate = s.rate.toDoubleOrNull() ?: return
        val tenureValue = s.tenure.toDoubleOrNull() ?: return
        runCatching {
            when (s.depositType) {
                DepositType.FD -> {
                    val years = if (s.tenureUnit == TenureUnit.YEARS) tenureValue else tenureValue / 12.0
                    FinanceCalculators.calculateFd(amount, rate, years, s.compoundingFreq.timesPerYear)
                }
                DepositType.RD -> {
                    val months = tenureValue.toInt()
                    FinanceCalculators.calculateRd(amount, rate, months)
                }
            }
        }.onSuccess { result ->
            _uiState.update { it.copy(result = result) }
        }
    }
}
