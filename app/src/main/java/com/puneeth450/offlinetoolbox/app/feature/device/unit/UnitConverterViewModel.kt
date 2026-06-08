package com.puneeth450.offlinetoolbox.app.feature.device.unit

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

enum class UnitCategory(val title: String, val units: List<String>) {
    LENGTH("Length", listOf("Meters", "Kilometers", "Miles", "Feet")),
    WEIGHT("Weight", listOf("Kilograms", "Grams", "Pounds", "Ounces")),
    TEMPERATURE("Temperature", listOf("Celsius", "Fahrenheit", "Kelvin"))
}

data class UnitConverterUiState(
    val category: UnitCategory = UnitCategory.LENGTH,
    val input: String = "1",
    val fromUnit: String = UnitCategory.LENGTH.units.first(),
    val toUnit: String = UnitCategory.LENGTH.units[1],
    val output: String = "0.001"
)

@HiltViewModel
class UnitConverterViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(UnitConverterUiState())
    val uiState: StateFlow<UnitConverterUiState> = _uiState

    fun onInputChanged(value: String) {
        val sanitized = value.filter { it.isDigit() || it == '.' }.take(12)
        _uiState.updateAndRecalculate { it.copy(input = sanitized) }
    }

    fun onCategorySelected(category: UnitCategory) {
        _uiState.updateAndRecalculate {
            it.copy(
                category = category,
                fromUnit = category.units.first(),
                toUnit = category.units.getOrElse(1) { category.units.first() }
            )
        }
    }

    fun onFromSelected(unit: String) {
        _uiState.updateAndRecalculate { it.copy(fromUnit = unit) }
    }

    fun onToSelected(unit: String) {
        _uiState.updateAndRecalculate { it.copy(toUnit = unit) }
    }

    private fun MutableStateFlow<UnitConverterUiState>.updateAndRecalculate(transform: (UnitConverterUiState) -> UnitConverterUiState) {
        update { current ->
            val next = transform(current)
            next.copy(output = convert(next))
        }
    }

    private fun convert(state: UnitConverterUiState): String {
        val value = state.input.toDoubleOrNull() ?: return ""
        val result = when (state.category) {
            UnitCategory.LENGTH -> convertLength(value, state.fromUnit, state.toUnit)
            UnitCategory.WEIGHT -> convertWeight(value, state.fromUnit, state.toUnit)
            UnitCategory.TEMPERATURE -> convertTemperature(value, state.fromUnit, state.toUnit)
        }
        return if (result.isFinite()) "%.4f".format(result).trimEnd('0').trimEnd('.') else ""
    }

    private fun convertLength(value: Double, from: String, to: String): Double {
        val meters = when (from) {
            "Meters" -> value
            "Kilometers" -> value * 1000
            "Miles" -> value * 1609.344
            else -> value * 0.3048
        }
        return when (to) {
            "Meters" -> meters
            "Kilometers" -> meters / 1000
            "Miles" -> meters / 1609.344
            else -> meters / 0.3048
        }
    }

    private fun convertWeight(value: Double, from: String, to: String): Double {
        val kilograms = when (from) {
            "Kilograms" -> value
            "Grams" -> value / 1000
            "Pounds" -> value * 0.45359237
            else -> value * 0.0283495231
        }
        return when (to) {
            "Kilograms" -> kilograms
            "Grams" -> kilograms * 1000
            "Pounds" -> kilograms / 0.45359237
            else -> kilograms / 0.0283495231
        }
    }

    private fun convertTemperature(value: Double, from: String, to: String): Double {
        val celsius = when (from) {
            "Celsius" -> value
            "Fahrenheit" -> (value - 32) * 5 / 9
            else -> value - 273.15
        }
        return when (to) {
            "Celsius" -> celsius
            "Fahrenheit" -> (celsius * 9 / 5) + 32
            else -> celsius + 273.15
        }
    }
}
