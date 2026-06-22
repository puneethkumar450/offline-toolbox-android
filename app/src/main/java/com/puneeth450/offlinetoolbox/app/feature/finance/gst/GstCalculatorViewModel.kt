package com.puneeth450.offlinetoolbox.app.feature.finance.gst

import androidx.lifecycle.ViewModel
import com.puneeth450.offlinetoolbox.app.domain.finance.FinanceCalculators
import com.puneeth450.offlinetoolbox.app.domain.finance.GstResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

enum class GstAmountType { EXCLUSIVE, INCLUSIVE }

enum class GstRate(val label: String, val percentValue: Double?) {
    ZERO("0%", 0.0),
    FIVE("5%", 5.0),
    TWELVE("12%", 12.0),
    EIGHTEEN("18%", 18.0),
    TWENTY_EIGHT("28%", 28.0),
    CUSTOM("Custom", null);

    companion object {
        val presets: List<GstRate> = entries
    }
}

data class GstUiState(
    val amount: String = "",
    val amountType: GstAmountType = GstAmountType.EXCLUSIVE,
    val selectedRate: GstRate = GstRate.EIGHTEEN,
    val customRate: String = "",
    val result: GstResult = GstResult(0.0, 0.0, 0.0)
) {
    val effectiveRate: Double
        get() = selectedRate.percentValue ?: customRate.toDoubleOrNull() ?: 0.0

    val isCustom: Boolean get() = selectedRate == GstRate.CUSTOM
}

@HiltViewModel
class GstCalculatorViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(GstUiState())
    val uiState: StateFlow<GstUiState> = _uiState

    fun onAmountChange(value: String) {
        _uiState.update { it.copy(amount = value) }
        recalculate()
    }

    fun onAmountTypeChange(type: GstAmountType) {
        _uiState.update { it.copy(amountType = type) }
        recalculate()
    }

    fun onRateChange(rate: GstRate) {
        _uiState.update { it.copy(selectedRate = rate) }
        recalculate()
    }

    fun onCustomRateChange(value: String) {
        _uiState.update { it.copy(customRate = value) }
        recalculate()
    }

    fun reset() {
        _uiState.update { GstUiState() }
    }

    private fun recalculate() {
        val state = _uiState.value
        val amount = state.amount.toDoubleOrNull() ?: 0.0
        val rate = state.effectiveRate
        runCatching {
            FinanceCalculators.calculateGst(amount, rate, state.amountType == GstAmountType.INCLUSIVE)
        }.onSuccess { result ->
            _uiState.update { it.copy(result = result) }
        }
    }
}
