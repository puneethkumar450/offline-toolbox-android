package com.puneeth450.offlinetoolbox.app.feature.finance.discount

import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneeth450.offlinetoolbox.app.ui.components.ResultCard
import com.puneeth450.offlinetoolbox.app.ui.components.ToolScaffold

@Composable
fun DiscountCalculatorScreen(
    onNavigateBack: () -> Unit,
    viewModel: DiscountCalculatorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    ToolScaffold(title = "Discount Calculator", onNavigateBack = onNavigateBack) {
        OutlinedTextField(value = state.input1, onValueChange = viewModel::onInput1, label = { Text("Amount / value") })
        OutlinedTextField(value = state.input2, onValueChange = viewModel::onInput2, label = { Text("Rate / people / discount") })
        OutlinedTextField(value = state.input3, onValueChange = viewModel::onInput3, label = { Text("Tenure / tip optional") })
        Button(onClick = viewModel::calculate) { Text("Calculate") }
        state.error?.let { Text(it) }
        if (state.result.isNotBlank()) ResultCard("Result", state.result)
    }
}
