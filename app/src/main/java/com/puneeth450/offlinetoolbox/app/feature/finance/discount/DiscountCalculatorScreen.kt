package com.puneeth450.offlinetoolbox.app.feature.finance.discount

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
fun DiscountCalculatorScreen(
    onNavigateBack: () -> Unit,
    viewModel: DiscountCalculatorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    ToolScaffold(title = "Discount Calculator", onNavigateBack = onNavigateBack) {
        ToolHeroCard(
            title = "Check real savings",
            description = "See the final sale price and how much you actually save before buying."
        )
        ToolSectionCard(title = "Price inputs", subtitle = "Use this for sales, coupon checks, or quick store comparisons.") {
            OutlinedTextField(
                value = state.input1,
                onValueChange = viewModel::onInput1,
                label = { Text("Original price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            OutlinedTextField(
                value = state.input2,
                onValueChange = viewModel::onInput2,
                label = { Text("Discount (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            Button(onClick = viewModel::calculate) { Text("Calculate Savings") }
        }
        state.error?.let { ToolMessageCard(message = it, isError = true) }
        if (state.result.isNotBlank()) ResultCard("Savings summary", state.result)
    }
}
