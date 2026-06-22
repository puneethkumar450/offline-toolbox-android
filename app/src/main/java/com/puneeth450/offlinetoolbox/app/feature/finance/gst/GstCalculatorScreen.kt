package com.puneeth450.offlinetoolbox.app.feature.finance.gst

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneeth450.offlinetoolbox.app.domain.finance.GstResult
import com.puneeth450.offlinetoolbox.app.domain.finance.formatIndianCurrency
import com.puneeth450.offlinetoolbox.app.ui.components.CommonTopBar
import com.puneeth450.offlinetoolbox.app.ui.components.ScreenPadding
import com.puneeth450.offlinetoolbox.app.ui.components.TestAdBanner
import com.puneeth450.offlinetoolbox.app.ui.theme.OfflineToolboxTheme

@Composable
fun GstCalculatorScreen(
    onNavigateBack: () -> Unit,
    viewModel: GstCalculatorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    GstCalculatorContent(
        state = state,
        onNavigateBack = onNavigateBack,
        onAmountChange = viewModel::onAmountChange,
        onAmountTypeChange = viewModel::onAmountTypeChange,
        onRateChange = viewModel::onRateChange,
        onCustomRateChange = viewModel::onCustomRateChange,
        onReset = viewModel::reset
    )
}

@Composable
private fun GstCalculatorContent(
    state: GstUiState,
    onNavigateBack: () -> Unit,
    onAmountChange: (String) -> Unit,
    onAmountTypeChange: (GstAmountType) -> Unit,
    onRateChange: (GstRate) -> Unit,
    onCustomRateChange: (String) -> Unit,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(ScreenPadding)
    ) {
        CommonTopBar(
            title = "GST Calculator",
            onNavigateBack = onNavigateBack,
            actionIcon = Icons.Default.Refresh,
            actionDescription = "Reset",
            onActionClick = onReset
        )

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = state.amount,
            onValueChange = onAmountChange,
            label = {
                Text(
                    if (state.amountType == GstAmountType.EXCLUSIVE)
                        "Amount (Exclusive of GST)"
                    else
                        "Amount (Inclusive of GST)"
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = "Amount Type",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(12.dp))

        AmountTypeToggle(
            selected = state.amountType,
            onSelect = onAmountTypeChange
        )

        Spacer(Modifier.height(20.dp))

        Text(
            text = "GST Rate",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(12.dp))

        GstRateChips(
            selected = state.selectedRate,
            onSelect = onRateChange
        )

        if (state.isCustom) {
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = state.customRate,
                onValueChange = onCustomRateChange,
                label = { Text("Custom GST Rate (%)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                suffix = { Text("%") }
            )
        }

        Spacer(Modifier.height(24.dp))

        CalculationBreakdownCard(
            result = state.result,
            ratePercent = state.effectiveRate
        )

        Spacer(Modifier.height(12.dp))

        HowItWorksCard(inclusive = state.amountType == GstAmountType.INCLUSIVE)

        Spacer(Modifier.height(20.dp))

        TestAdBanner(
            title = "Test Ad : Advanced Home Pump Solutions",
            description = "Trusted pumping solutions that bring comfort and convenience to every home.",
            ctaText = "Learn More"
        )
    }
}

@Composable
private fun AmountTypeToggle(
    selected: GstAmountType,
    onSelect: (GstAmountType) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            listOf(GstAmountType.EXCLUSIVE to "Exclusive", GstAmountType.INCLUSIVE to "Inclusive").forEach { (type, label) ->
                val isSelected = selected == type
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    shape = RoundedCornerShape(24.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.onSurfaceVariant else Color.Transparent,
                    onClick = { onSelect(type) }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.surface,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = label,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) MaterialTheme.colorScheme.surface
                                        else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GstRateChips(
    selected: GstRate,
    onSelect: (GstRate) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        GstRate.presets.forEach { rate ->
            val isSelected = selected == rate
            Surface(
                shape = RoundedCornerShape(50),
                color = if (isSelected) MaterialTheme.colorScheme.onSurfaceVariant
                        else Color.Transparent,
                onClick = { onSelect(rate) },
                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(
                    1.dp, MaterialTheme.colorScheme.outline
                )
            ) {
                Text(
                    text = rate.label,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) MaterialTheme.colorScheme.surface
                            else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun CalculationBreakdownCard(result: GstResult, ratePercent: Double) {
    val onColor = MaterialTheme.colorScheme.surface
    val mutedColor = onColor.copy(alpha = 0.85f)
    val rateLabel = if (ratePercent % 1.0 == 0.0) "${ratePercent.toInt()}%" else "${ratePercent}%"

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Calculation Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = onColor
            )

            HorizontalDivider(color = onColor.copy(alpha = 0.25f))

            BreakdownRow("Base Amount", formatIndianCurrency(result.baseAmount), mutedColor, mutedColor)
            BreakdownRow("GST @ $rateLabel", formatIndianCurrency(result.gstAmount), mutedColor, mutedColor)

            HorizontalDivider(color = onColor.copy(alpha = 0.25f))

            BreakdownRow(
                label = "Total Amount",
                value = formatIndianCurrency(result.totalAmount),
                labelColor = onColor,
                valueColor = onColor,
                labelBold = true,
                valueLarge = true
            )
        }
    }
}

@Composable
private fun BreakdownRow(
    label: String,
    value: String,
    labelColor: Color,
    valueColor: Color,
    labelBold: Boolean = false,
    valueLarge: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (labelBold) FontWeight.Bold else FontWeight.Normal,
            color = labelColor
        )
        Text(
            text = value,
            style = if (valueLarge) MaterialTheme.typography.titleLarge
                    else MaterialTheme.typography.bodyLarge,
            fontWeight = if (valueLarge) FontWeight.Bold else FontWeight.SemiBold,
            color = valueColor
        )
    }
}

@Composable
private fun HowItWorksCard(inclusive: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = "ℹ️", style = MaterialTheme.typography.bodyLarge)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "How it works",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (inclusive)
                        "Inclusive: Enter total amount (with GST). Calculator extracts the base amount and GST."
                    else
                        "Exclusive: Enter base amount (without GST). Calculator adds GST to get total.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GstExclusivePreview() {
    OfflineToolboxTheme(darkTheme = false) {
        GstCalculatorContent(
            state = GstUiState(),
            onNavigateBack = {},
            onAmountChange = {},
            onAmountTypeChange = {},
            onRateChange = {},
            onCustomRateChange = {},
            onReset = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GstInclusiveCustomPreview() {
    OfflineToolboxTheme(darkTheme = false) {
        GstCalculatorContent(
            state = GstUiState(
                amount = "1000",
                amountType = GstAmountType.EXCLUSIVE,
                selectedRate = GstRate.CUSTOM,
                customRate = "22",
                result = GstResult(baseAmount = 1000.0, gstAmount = 220.0, totalAmount = 1220.0)
            ),
            onNavigateBack = {},
            onAmountChange = {},
            onAmountTypeChange = {},
            onRateChange = {},
            onCustomRateChange = {},
            onReset = {}
        )
    }
}
