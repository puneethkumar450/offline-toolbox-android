package com.puneeth450.offlinetoolbox.app.feature.finance.rule72

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneeth450.offlinetoolbox.app.ui.components.ResultCard
import com.puneeth450.offlinetoolbox.app.ui.components.ToolHeroCard
import com.puneeth450.offlinetoolbox.app.ui.components.ToolMessageCard
import com.puneeth450.offlinetoolbox.app.ui.components.ToolScaffold
import com.puneeth450.offlinetoolbox.app.ui.components.ToolSectionCard

@Composable
fun Rule72CalculatorScreen(
    onNavigateBack: () -> Unit,
    viewModel: Rule72CalculatorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    ToolScaffold(title = "Rule of 72 Calculator", onNavigateBack = onNavigateBack) {
        ToolHeroCard(
            title = "Estimate doubling time",
            description = "Use the Rule of 72 to quickly estimate how long an investment may take to double."
        )
        ToolSectionCard(title = "Growth input", subtitle = "Enter the expected annual return rate in percent.") {
            OutlinedTextField(
                value = state.input1,
                onValueChange = viewModel::onInput1,
                label = { Text("Annual return (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            Button(onClick = viewModel::calculate) { Text("Estimate Time") }
        }
        state.error?.let { ToolMessageCard(message = it, isError = true) }
        if (state.result.isNotBlank()) ResultCard("Estimate", state.result)
    }
}
