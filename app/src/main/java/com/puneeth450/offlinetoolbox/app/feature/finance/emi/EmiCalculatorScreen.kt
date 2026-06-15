package com.puneeth450.offlinetoolbox.app.feature.finance.emi

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneeth450.offlinetoolbox.app.domain.finance.EmiResult
import com.puneeth450.offlinetoolbox.app.ui.components.CommonTopBar
import com.puneeth450.offlinetoolbox.app.ui.components.ScreenPadding
import com.puneeth450.offlinetoolbox.app.ui.components.TestAdBanner
import com.puneeth450.offlinetoolbox.app.ui.theme.OfflineToolboxTheme

@Composable
fun EmiCalculatorScreen(
    onNavigateBack: () -> Unit,
    viewModel: EmiCalculatorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    EmiCalculatorContent(
        state = state,
        onNavigateBack = onNavigateBack,
        onLoanAmountChange = viewModel::onLoanAmountChange,
        onInterestRateChange = viewModel::onInterestRateChange,
        onTenureChange = viewModel::onTenureChange,
        onTenureUnitChange = viewModel::onTenureUnitChange,
        onReset = viewModel::reset,
        onCalculate = viewModel::calculate
    )
}

@Composable
private fun EmiCalculatorContent(
    state: EmiCalculatorUiState,
    onNavigateBack: () -> Unit,
    onLoanAmountChange: (String) -> Unit,
    onInterestRateChange: (String) -> Unit,
    onTenureChange: (String) -> Unit,
    onTenureUnitChange: (TenureUnit) -> Unit,
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
        CommonTopBar(title = "EMI Calculator", onNavigateBack = onNavigateBack)

        Spacer(Modifier.height(28.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccountBalance,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Text(
                text = "Calculate Your Loan EMI",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(Modifier.height(28.dp))

        OutlinedTextField(
            value = state.loanAmount,
            onValueChange = onLoanAmountChange,
            label = { Text("Loan Amount") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = state.interestRate,
            onValueChange = onInterestRateChange,
            label = { Text("Annual Interest Rate") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Percent,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Tenure Unit",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            TenureUnitChip(
                label = "Years",
                selected = state.tenureUnit == TenureUnit.YEARS,
                onClick = { onTenureUnitChange(TenureUnit.YEARS) },
                modifier = Modifier.weight(1f)
            )
            TenureUnitChip(
                label = "Months",
                selected = state.tenureUnit == TenureUnit.MONTHS,
                onClick = { onTenureUnitChange(TenureUnit.MONTHS) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = state.tenure,
            onValueChange = onTenureChange,
            label = { Text("Loan Tenure") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

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
            EmiResultCard(result)
        }

        Spacer(Modifier.height(24.dp))

        TestAdBanner(
            title = "Test Ad : Bring Your Pet Along",
            description = "Resort stays for you & your pet near Mumbai & Nashik. Enquire at Manas Resort",
            ctaText = "Learn More"
        )
    }
}

@Composable
private fun TenureUnitChip(
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

@Composable
private fun EmiResultCard(result: EmiResult) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Monthly EMI",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "%,.2f".format(result.emi),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            EmiResultRow("Total Interest", "%,.2f".format(result.totalInterest))
            EmiResultRow("Total Payment", "%,.2f".format(result.totalPayment))
        }
    }
}

@Composable
private fun EmiResultRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmiCalculatorEmptyPreview() {
    OfflineToolboxTheme(darkTheme = false) {
        EmiCalculatorContent(
            state = EmiCalculatorUiState(),
            onNavigateBack = {},
            onLoanAmountChange = {},
            onInterestRateChange = {},
            onTenureChange = {},
            onTenureUnitChange = {},
            onReset = {},
            onCalculate = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmiCalculatorResultPreview() {
    OfflineToolboxTheme(darkTheme = false) {
        EmiCalculatorContent(
            state = EmiCalculatorUiState(
                loanAmount = "500000",
                interestRate = "8.5",
                tenure = "5",
                result = EmiResult(emi = 10258.27, totalInterest = 115496.2, totalPayment = 615496.2)
            ),
            onNavigateBack = {},
            onLoanAmountChange = {},
            onInterestRateChange = {},
            onTenureChange = {},
            onTenureUnitChange = {},
            onReset = {},
            onCalculate = {}
        )
    }
}
