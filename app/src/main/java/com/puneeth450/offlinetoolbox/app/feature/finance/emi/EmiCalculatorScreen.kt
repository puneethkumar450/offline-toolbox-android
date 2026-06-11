package com.puneeth450.offlinetoolbox.app.feature.finance.emi

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneeth450.offlinetoolbox.app.ui.components.ResultCard
import com.puneeth450.offlinetoolbox.app.ui.components.ToolHeroCard
import com.puneeth450.offlinetoolbox.app.ui.components.ToolMessageCard
import com.puneeth450.offlinetoolbox.app.ui.components.ToolScaffold
import com.puneeth450.offlinetoolbox.app.ui.components.ToolSectionCard

@Composable
fun EmiCalculatorScreen(
    onNavigateBack: () -> Unit,
    viewModel: EmiCalculatorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    ToolScaffold(title = "EMI Calculator", onNavigateBack = onNavigateBack) {
        ToolHeroCard(
            title = "Plan monthly repayments",
            description = "Estimate EMI, total interest, and total payout before committing to a loan."
        )
        ToolSectionCard(title = "Loan inputs", subtitle = "Enter principal, annual interest rate, and tenure in years.") {
            OutlinedTextField(
                value = state.input1,
                onValueChange = viewModel::onInput1,
                label = { Text("Loan amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier
            )
            OutlinedTextField(
                value = state.input2,
                onValueChange = viewModel::onInput2,
                label = { Text("Interest rate (%)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            OutlinedTextField(
                value = state.input3,
                onValueChange = viewModel::onInput3,
                label = { Text("Tenure (years)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Button(onClick = viewModel::calculate) { Text("Calculate EMI") }
        }
        state.error?.let { ToolMessageCard(message = it, isError = true) }
        if (state.result.isNotBlank()) ResultCard("Repayment summary", state.result)
    }
}
