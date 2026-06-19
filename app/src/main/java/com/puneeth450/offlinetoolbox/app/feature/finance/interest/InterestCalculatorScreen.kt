package com.puneeth450.offlinetoolbox.app.feature.finance.interest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneeth450.offlinetoolbox.app.domain.finance.InterestResult
import com.puneeth450.offlinetoolbox.app.ui.components.CommonTopBar
import com.puneeth450.offlinetoolbox.app.ui.components.ScreenPadding
import com.puneeth450.offlinetoolbox.app.ui.theme.OfflineToolboxTheme

@Composable
fun InterestCalculatorScreen(
    onNavigateBack: () -> Unit,
    viewModel: InterestCalculatorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    InterestCalculatorContent(
        state = state,
        onNavigateBack = onNavigateBack,
        onInterestTypeChange = viewModel::onInterestTypeChange,
        onPrincipalChange = viewModel::onPrincipalChange,
        onRateChange = viewModel::onRateChange,
        onTimeChange = viewModel::onTimeChange,
        onCompoundingFrequencyChange = viewModel::onCompoundingFrequencyChange,
        onReset = viewModel::reset,
        onCalculate = viewModel::calculate
    )
}

@Composable
private fun InterestCalculatorContent(
    state: InterestCalculatorUiState,
    onNavigateBack: () -> Unit,
    onInterestTypeChange: (InterestType) -> Unit,
    onPrincipalChange: (String) -> Unit,
    onRateChange: (String) -> Unit,
    onTimeChange: (String) -> Unit,
    onCompoundingFrequencyChange: (CompoundingFrequency) -> Unit,
    onReset: () -> Unit,
    onCalculate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(ScreenPadding)
    ) {
        CommonTopBar(title = "Interest Calculator", onNavigateBack = onNavigateBack)

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Interest Type",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            InterestTypeChip(
                label = "Simple Interest",
                selected = state.interestType == InterestType.SIMPLE,
                onClick = { onInterestTypeChange(InterestType.SIMPLE) },
                modifier = Modifier.weight(1f)
            )
            InterestTypeChip(
                label = "Compound Interest",
                selected = state.interestType == InterestType.COMPOUND,
                onClick = { onInterestTypeChange(InterestType.COMPOUND) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(28.dp))

        OutlinedTextField(
            value = state.principal,
            onValueChange = onPrincipalChange,
            label = { Text("Principal Amount") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = state.rate,
            onValueChange = onRateChange,
            label = { Text("Rate of Interest") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text("% per year") }
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = state.time,
            onValueChange = onTimeChange,
            label = { Text("Time Period") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text("years") }
        )

        if (state.interestType == InterestType.COMPOUND) {
            Spacer(Modifier.height(16.dp))
            CompoundingFrequencyDropdown(
                selected = state.compoundingFrequency,
                onSelected = onCompoundingFrequencyChange
            )
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onReset,
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Text("Reset", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Button(
                onClick = onCalculate,
                enabled = state.canCalculate,
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Text("Calculate", fontWeight = FontWeight.Bold)
            }
        }

        state.error?.let { error ->
            Spacer(Modifier.height(18.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        state.result?.let { result ->
            Spacer(Modifier.height(18.dp))
            InterestResultCard(state = state, result = result)
        }
    }
}

@Composable
private fun InterestTypeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (selected) {
        Surface(
            modifier = modifier.height(56.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            onClick = onClick
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.surface,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            shape = RoundedCornerShape(28.dp),
            modifier = modifier.height(56.dp)
        ) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompoundingFrequencyDropdown(
    selected: CompoundingFrequency,
    onSelected: (CompoundingFrequency) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selected.label,
            onValueChange = {},
            readOnly = true,
            label = { Text("Compounding Frequency") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            CompoundingFrequency.entries.forEach { frequency ->
                DropdownMenuItem(
                    text = { Text(frequency.label) },
                    onClick = {
                        onSelected(frequency)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun InterestResultCard(state: InterestCalculatorUiState, result: InterestResult) {
    val onColor = MaterialTheme.colorScheme.surface
    val mutedColor = onColor.copy(alpha = 0.75f)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = if (state.interestType == InterestType.COMPOUND) "Compound Interest" else "Simple Interest",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = onColor
            )

            Spacer(Modifier.height(6.dp))

            InterestResultRow("Principal", formatCurrency(state.principal.toDoubleOrNull() ?: 0.0), mutedColor, onColor)
            InterestResultRow("Rate", "${state.rate}% per year", mutedColor, onColor)
            InterestResultRow("Time", "${state.time} years", mutedColor, onColor)
            if (state.interestType == InterestType.COMPOUND) {
                InterestResultRow("Compounding", state.compoundingFrequency.label, mutedColor, onColor)
            }

            Spacer(Modifier.height(10.dp))

            InterestResultRow("Interest Earned", formatCurrency(result.interest), mutedColor, mutedColor)

            Spacer(Modifier.height(2.dp))

            Text(
                text = "Total Amount",
                style = MaterialTheme.typography.bodyLarge,
                color = mutedColor
            )
            Text(
                text = formatCurrency(result.totalAmount),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = onColor
            )
        }
    }
}

@Composable
private fun InterestResultRow(
    label: String,
    value: String,
    labelColor: Color,
    valueColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = labelColor
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = valueColor
        )
    }
}

private fun formatCurrency(value: Double): String {
    val pattern = if (value % 1.0 == 0.0) "₹%,.0f" else "₹%,.2f"
    return pattern.format(value)
}

@Preview(showBackground = true)
@Composable
private fun InterestCalculatorEmptyPreview() {
    OfflineToolboxTheme(darkTheme = false) {
        InterestCalculatorContent(
            state = InterestCalculatorUiState(),
            onNavigateBack = {},
            onInterestTypeChange = {},
            onPrincipalChange = {},
            onRateChange = {},
            onTimeChange = {},
            onCompoundingFrequencyChange = {},
            onReset = {},
            onCalculate = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InterestCalculatorResultPreview() {
    OfflineToolboxTheme(darkTheme = false) {
        InterestCalculatorContent(
            state = InterestCalculatorUiState(
                principal = "100000",
                rate = "7.5",
                time = "5",
                result = InterestResult(interest = 37500.0, totalAmount = 137500.0)
            ),
            onNavigateBack = {},
            onInterestTypeChange = {},
            onPrincipalChange = {},
            onRateChange = {},
            onTimeChange = {},
            onCompoundingFrequencyChange = {},
            onReset = {},
            onCalculate = {}
        )
    }
}
