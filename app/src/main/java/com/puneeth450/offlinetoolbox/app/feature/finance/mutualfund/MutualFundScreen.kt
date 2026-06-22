package com.puneeth450.offlinetoolbox.app.feature.finance.mutualfund

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneeth450.offlinetoolbox.app.domain.finance.MutualFundResult
import com.puneeth450.offlinetoolbox.app.domain.finance.formatIndianCompact
import com.puneeth450.offlinetoolbox.app.ui.components.CommonTopBar
import com.puneeth450.offlinetoolbox.app.ui.components.ScreenPadding
import com.puneeth450.offlinetoolbox.app.ui.components.TestAdBanner
import com.puneeth450.offlinetoolbox.app.ui.theme.OfflineToolboxTheme

@Composable
fun MutualFundScreen(
    onNavigateBack: () -> Unit,
    viewModel: MutualFundViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    MutualFundContent(
        state = state,
        onNavigateBack = onNavigateBack,
        onInvestmentTypeChange = viewModel::onInvestmentTypeChange,
        onAmountChange = viewModel::onAmountChange,
        onRateChange = viewModel::onRateChange,
        onPeriodChange = viewModel::onPeriodChange,
        onReset = viewModel::reset,
        onCalculate = viewModel::calculate
    )
}

@Composable
private fun MutualFundContent(
    state: MutualFundUiState,
    onNavigateBack: () -> Unit,
    onInvestmentTypeChange: (InvestmentType) -> Unit,
    onAmountChange: (String) -> Unit,
    onRateChange: (String) -> Unit,
    onPeriodChange: (String) -> Unit,
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
        CommonTopBar(title = "Mutual Fund Calculator", onNavigateBack = onNavigateBack)

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Plan Your Investment",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Investment Type",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            InvestmentTypeChip(
                label = "SIP",
                selected = state.investmentType == InvestmentType.SIP,
                onClick = { onInvestmentTypeChange(InvestmentType.SIP) },
                modifier = Modifier.weight(1f)
            )
            InvestmentTypeChip(
                label = "Lumpsum",
                selected = state.investmentType == InvestmentType.LUMPSUM,
                onClick = { onInvestmentTypeChange(InvestmentType.LUMPSUM) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(28.dp))

        OutlinedTextField(
            value = state.amount,
            onValueChange = onAmountChange,
            label = {
                Text(
                    if (state.investmentType == InvestmentType.SIP) "Monthly Investment"
                    else "One-time Investment"
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = state.rate,
            onValueChange = onRateChange,
            label = { Text("Expected Annual Return") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text("% per year") }
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = state.period,
            onValueChange = onPeriodChange,
            label = { Text("Investment Period") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text("years") }
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
            Spacer(Modifier.height(20.dp))
            MaturityValueCard(totalValue = result.totalValue)
            Spacer(Modifier.height(12.dp))
            InvestmentSummaryCard(
                result = result,
                period = state.period
            )
            Spacer(Modifier.height(12.dp))
            WealthGainCard(
                invested = result.investedAmount,
                returns = result.estimatedReturns
            )
        }

        Spacer(Modifier.height(20.dp))

        TestAdBanner(
            title = "Test Ad : Vparty - Group Voice Chat Room",
            description = "Popular voice party app. Party with friends. Try Now",
            ctaText = "Install"
        )
    }
}

@Composable
private fun InvestmentTypeChip(
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
private fun MaturityValueCard(totalValue: Double) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Maturity Value",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.surface
            )
            Text(
                text = formatIndianCompact(totalValue),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun InvestmentSummaryCard(result: MutualFundResult, period: String) {
    val onColor = MaterialTheme.colorScheme.surface
    val mutedColor = onColor.copy(alpha = 0.80f)
    val greenColor = Color(0xFF4CAF50)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Investment Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = onColor
            )
            SummaryRow(label = "Total Invested", value = formatIndianCompact(result.investedAmount), labelColor = mutedColor, valueColor = onColor)
            SummaryRow(label = "Estimated Returns", value = formatIndianCompact(result.estimatedReturns), labelColor = mutedColor, valueColor = greenColor)
            SummaryRow(
                label = "Investment Period",
                value = "${period.toDoubleOrNull()?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: period} years",
                labelColor = mutedColor,
                valueColor = onColor,
                valueBold = true
            )
        }
    }
}

@Composable
private fun WealthGainCard(invested: Double, returns: Double) {
    val gainPercent = if (invested > 0) (returns / invested) * 100 else 0.0
    val gainText = "+${"%.1f".format(gainPercent)}%"
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFE8F5E9)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Wealth Gain",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = gainText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E7D32)
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    labelColor: Color,
    valueColor: Color,
    valueBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = labelColor)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (valueBold) FontWeight.Bold else FontWeight.SemiBold,
            color = valueColor
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun MutualFundEmptyPreview() {
    OfflineToolboxTheme(darkTheme = false) {
        MutualFundContent(
            state = MutualFundUiState(),
            onNavigateBack = {},
            onInvestmentTypeChange = {},
            onAmountChange = {},
            onRateChange = {},
            onPeriodChange = {},
            onReset = {},
            onCalculate = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MutualFundResultPreview() {
    OfflineToolboxTheme(darkTheme = false) {
        MutualFundContent(
            state = MutualFundUiState(
                investmentType = InvestmentType.SIP,
                amount = "10000",
                rate = "13",
                period = "1",
                result = MutualFundResult(
                    investedAmount = 1_00_000.0,
                    estimatedReturns = 13_000.0,
                    totalValue = 1_13_000.0
                )
            ),
            onNavigateBack = {},
            onInvestmentTypeChange = {},
            onAmountChange = {},
            onRateChange = {},
            onPeriodChange = {},
            onReset = {},
            onCalculate = {}
        )
    }
}
