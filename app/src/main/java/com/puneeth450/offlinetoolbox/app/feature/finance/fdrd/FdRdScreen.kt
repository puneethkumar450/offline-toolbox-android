package com.puneeth450.offlinetoolbox.app.feature.finance.fdrd

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
import androidx.compose.foundation.layout.width
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
import com.puneeth450.offlinetoolbox.app.domain.finance.FdRdResult
import com.puneeth450.offlinetoolbox.app.domain.finance.formatIndianCurrency
import com.puneeth450.offlinetoolbox.app.ui.components.CommonTopBar
import com.puneeth450.offlinetoolbox.app.ui.components.ScreenPadding
import com.puneeth450.offlinetoolbox.app.ui.components.TestAdBanner
import com.puneeth450.offlinetoolbox.app.ui.theme.OfflineToolboxTheme

@Composable
fun FdRdScreen(
    onNavigateBack: () -> Unit,
    viewModel: FdRdViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    FdRdContent(
        state = state,
        onNavigateBack = onNavigateBack,
        onDepositTypeChange = viewModel::onDepositTypeChange,
        onAmountChange = viewModel::onAmountChange,
        onRateChange = viewModel::onRateChange,
        onTenureChange = viewModel::onTenureChange,
        onTenureUnitChange = viewModel::onTenureUnitChange,
        onCompoundingFreqChange = viewModel::onCompoundingFreqChange,
        onReset = viewModel::reset
    )
}

@Composable
private fun FdRdContent(
    state: FdRdUiState,
    onNavigateBack: () -> Unit,
    onDepositTypeChange: (DepositType) -> Unit,
    onAmountChange: (String) -> Unit,
    onRateChange: (String) -> Unit,
    onTenureChange: (String) -> Unit,
    onTenureUnitChange: (TenureUnit) -> Unit,
    onCompoundingFreqChange: (CompoundingFreq) -> Unit,
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
            title = "FD & RD Calculator",
            onNavigateBack = onNavigateBack,
            actionIcon = Icons.Default.Refresh,
            actionDescription = "Reset",
            onActionClick = onReset
        )

        Spacer(Modifier.height(20.dp))

        // FD / RD type toggle
        DepositTypeToggle(
            selected = state.depositType,
            onSelect = onDepositTypeChange
        )

        Spacer(Modifier.height(20.dp))

        // Amount field
        OutlinedTextField(
            value = state.amount,
            onValueChange = onAmountChange,
            label = {
                Text(if (state.depositType == DepositType.FD) "Principal Amount" else "Monthly Deposit")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        // Rate field
        OutlinedTextField(
            value = state.rate,
            onValueChange = onRateChange,
            label = { Text("Annual Interest Rate") },
            placeholder = { Text("Enter rate") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth(),
            suffix = { Text("% p.a.") }
        )

        Spacer(Modifier.height(16.dp))

        if (state.depositType == DepositType.FD) {
            // Tenure + Years/Months inline
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.tenure,
                    onValueChange = onTenureChange,
                    label = { Text("Tenure") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.weight(1f)
                )
                MiniToggle(
                    options = listOf(TenureUnit.YEARS to "Years", TenureUnit.MONTHS to "Months"),
                    selected = state.tenureUnit,
                    onSelect = onTenureUnitChange
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Compounding Frequency",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(12.dp))

            TwoOptionToggle(
                leftLabel = CompoundingFreq.QUARTERLY.label,
                rightLabel = CompoundingFreq.YEARLY.label,
                leftSelected = state.compoundingFreq == CompoundingFreq.QUARTERLY,
                onLeftClick = { onCompoundingFreqChange(CompoundingFreq.QUARTERLY) },
                onRightClick = { onCompoundingFreqChange(CompoundingFreq.YEARLY) }
            )
        } else {
            // RD: tenure in months only
            OutlinedTextField(
                value = state.tenure,
                onValueChange = onTenureChange,
                label = { Text("Tenure (Months)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(24.dp))

        if (state.depositType == DepositType.FD) {
            FdResultCard(state = state, result = state.result)
            Spacer(Modifier.height(12.dp))
            FormulaCard(
                title = "FD Formula",
                formula = "A = P × (1 + r/n)^(n×t)",
                legend = "Where: P=Principal, r=Annual rate, n=Compounding frequency, t=Time in years"
            )
        } else {
            RdResultCard(state = state, result = state.result)
            Spacer(Modifier.height(12.dp))
            FormulaCard(
                title = "RD Formula",
                formula = "M = P × [(1+r/n)^(n×t) - 1] / (1 - (1+r/n)^(-1/3))",
                legend = "Where: P=Monthly deposit, r=Rate, n=Months"
            )
        }

        Spacer(Modifier.height(20.dp))

        TestAdBanner(
            title = "Test Ad : Advanced Home Pump Solutions",
            description = "Trusted pumping solutions that bring comfort and convenience to every home.",
            ctaText = "Learn More"
        )
    }
}

// ── Shared toggle components ─────────────────────────────────────────────────

@Composable
private fun DepositTypeToggle(
    selected: DepositType,
    onSelect: (DepositType) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            listOf(
                DepositType.FD to "Fixed Deposit (FD)",
                DepositType.RD to "Recurring Deposit (RD)"
            ).forEach { (type, label) ->
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
                                style = MaterialTheme.typography.bodyMedium,
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

@Composable
private fun TwoOptionToggle(
    leftLabel: String,
    rightLabel: String,
    leftSelected: Boolean,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            listOf(
                Triple(leftSelected, leftLabel, onLeftClick),
                Triple(!leftSelected, rightLabel, onRightClick)
            ).forEach { (isSelected, label, onClick) ->
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    shape = RoundedCornerShape(24.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.onSurfaceVariant else Color.Transparent,
                    onClick = onClick
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

@Composable
private fun <T> MiniToggle(
    options: List<Pair<T, String>>,
    selected: T,
    onSelect: (T) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(modifier = Modifier.padding(3.dp)) {
            options.forEach { (value, label) ->
                val isSelected = selected == value
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.onSurfaceVariant else Color.Transparent,
                    onClick = { onSelect(value) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
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

// ── Result cards ─────────────────────────────────────────────────────────────

@Composable
private fun FdResultCard(state: FdRdUiState, result: FdRdResult) {
    val onColor = MaterialTheme.colorScheme.surface
    val mutedColor = onColor.copy(alpha = 0.85f)

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
                text = "FD Maturity Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = onColor
            )
            HorizontalDivider(color = onColor.copy(alpha = 0.25f))
            MaturityRow("Principal Amount", formatIndianCurrency(result.principal), mutedColor, mutedColor)
            MaturityRow("Interest Earned", formatIndianCurrency(result.interestEarned), mutedColor, mutedColor)
            HorizontalDivider(color = onColor.copy(alpha = 0.25f))
            MaturityRow("Maturity Amount", formatIndianCurrency(result.maturityAmount), onColor, onColor, bold = true, large = true)
        }
    }
}

@Composable
private fun RdResultCard(state: FdRdUiState, result: FdRdResult) {
    val onColor = MaterialTheme.colorScheme.surface
    val mutedColor = onColor.copy(alpha = 0.85f)

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
                text = "RD Maturity Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = onColor
            )
            HorizontalDivider(color = onColor.copy(alpha = 0.25f))
            MaturityRow("Monthly Deposit", formatIndianCurrency(state.amount.toDoubleOrNull() ?: 0.0), mutedColor, mutedColor)
            MaturityRow("Total Investment", formatIndianCurrency(result.totalInvestment), mutedColor, mutedColor)
            MaturityRow("Interest Earned", formatIndianCurrency(result.interestEarned), mutedColor, mutedColor)
            HorizontalDivider(color = onColor.copy(alpha = 0.25f))
            MaturityRow("Maturity Amount", formatIndianCurrency(result.maturityAmount), onColor, onColor, bold = true, large = true)
        }
    }
}

@Composable
private fun MaturityRow(
    label: String,
    value: String,
    labelColor: Color,
    valueColor: Color,
    bold: Boolean = false,
    large: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = labelColor
        )
        Text(
            text = value,
            style = if (large) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge,
            fontWeight = if (large) FontWeight.Bold else FontWeight.SemiBold,
            color = valueColor
        )
    }
}

@Composable
private fun FormulaCard(title: String, formula: String, legend: String) {
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
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = formula,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = legend,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FdPreview() {
    OfflineToolboxTheme(darkTheme = false) {
        FdRdContent(
            state = FdRdUiState(),
            onNavigateBack = {},
            onDepositTypeChange = {},
            onAmountChange = {},
            onRateChange = {},
            onTenureChange = {},
            onTenureUnitChange = {},
            onCompoundingFreqChange = {},
            onReset = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RdPreview() {
    OfflineToolboxTheme(darkTheme = false) {
        FdRdContent(
            state = FdRdUiState(depositType = DepositType.RD),
            onNavigateBack = {},
            onDepositTypeChange = {},
            onAmountChange = {},
            onRateChange = {},
            onTenureChange = {},
            onTenureUnitChange = {},
            onCompoundingFreqChange = {},
            onReset = {}
        )
    }
}
