package com.puneeth450.offlinetoolbox.app.feature.finance.split

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
fun SplitBillCalculatorScreen(
    onNavigateBack: () -> Unit,
    viewModel: SplitBillCalculatorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    ToolScaffold(title = "Split Bill Calculator", onNavigateBack = onNavigateBack) {
        ToolHeroCard(
            title = "Split shared costs fairly",
            description = "Quickly calculate per-person share with optional tip already included."
        )
        ToolSectionCard(title = "Bill inputs", subtitle = "Best for meals, rides, group orders, or travel costs.") {
            OutlinedTextField(
                value = state.input1,
                onValueChange = viewModel::onInput1,
                label = { Text("Bill total") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            OutlinedTextField(
                value = state.input2,
                onValueChange = viewModel::onInput2,
                label = { Text("Number of people") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = state.input3,
                onValueChange = viewModel::onInput3,
                label = { Text("Tip (%) optional") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            Button(onClick = viewModel::calculate) { Text("Split Bill") }
        }
        state.error?.let { ToolMessageCard(message = it, isError = true) }
        if (state.result.isNotBlank()) ResultCard("Split summary", state.result)
    }
}
