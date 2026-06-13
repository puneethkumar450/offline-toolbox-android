package com.puneeth450.offlinetoolbox.app.feature.productivity.tally

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
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneeth450.offlinetoolbox.app.ui.components.CommonTopBar
import com.puneeth450.offlinetoolbox.app.ui.components.ScreenPadding
import com.puneeth450.offlinetoolbox.app.ui.components.TestAdBanner
import com.puneeth450.offlinetoolbox.app.ui.theme.OfflineToolboxTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun TallyCounterScreen(
    onNavigateBack: () -> Unit,
    viewModel: TallyCounterViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showTargetDialog by rememberSaveable { mutableStateOf(false) }
    var showIntervalDialog by rememberSaveable { mutableStateOf(false) }
    var showAutoToast by remember { mutableStateOf(state.showAutoStartedMessage) }

    LaunchedEffect(state.showAutoStartedMessage) {
        if (state.showAutoStartedMessage) {
            showAutoToast = true
            delay(2200L.milliseconds)
            showAutoToast = false
            viewModel.dismissAutoStartedMessage()
        }
    }

    TallyCounterContent(
        state = state,
        onNavigateBack = onNavigateBack,
        onDecrement = viewModel::decrement,
        onIncrement = viewModel::increment,
        onReset = viewModel::reset,
        onOpenTarget = { showTargetDialog = true },
        onOpenInterval = { showIntervalDialog = true },
        onLoopChanged = viewModel::setLoopAtTarget,
        onToggleAuto = viewModel::toggleAuto,
        showAutoToast = showAutoToast
    )

    if (showTargetDialog) {
        NumberInputDialog(
            title = "Set Target",
            label = "Target count",
            initialValue = state.target.toString(),
            onDismiss = { showTargetDialog = false },
            onSave = {
                viewModel.saveTarget(it)
                showTargetDialog = false
            }
        )
    }

    if (showIntervalDialog) {
        NumberInputDialog(
            title = "Set Auto Interval (ms)",
            label = "Milliseconds",
            initialValue = state.autoIntervalMillis.toString(),
            onDismiss = { showIntervalDialog = false },
            onSave = {
                viewModel.saveAutoInterval(it)
                showIntervalDialog = false
            }
        )
    }
}

@Composable
private fun TallyCounterContent(
    state: TallyCounterUiState,
    onNavigateBack: () -> Unit,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    onReset: () -> Unit,
    onOpenTarget: () -> Unit,
    onOpenInterval: () -> Unit,
    onLoopChanged: (Boolean) -> Unit,
    onToggleAuto: () -> Unit,
    showAutoToast: Boolean
) {
    val progress = (state.count.toFloat() / state.target.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(ScreenPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CommonTopBar(title = "Tally Counter", onNavigateBack = onNavigateBack)

        Spacer(Modifier.height(42.dp))

        TallyRing(
            count = state.count,
            target = state.target,
            progress = progress
        )

        Spacer(Modifier.height(32.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 22.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CounterCircleButton(
                        label = "−",
                        backgroundColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        size = 120.dp / 2f,
                        onClick = onDecrement
                    )
                    CounterCircleButton(
                        icon = Icons.Default.Refresh,
                        backgroundColor = MaterialTheme.colorScheme.error.copy(alpha = 0.16f),
                        contentColor = MaterialTheme.colorScheme.error.copy(alpha = 0.88f),
                        size = 120.dp / 2f,
                        onClick = onReset
                    )
                    CounterCircleButton(
                        label = "+",
                        backgroundColor = Color(0xFF657DB5),
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        size = 120.dp / 2f,
                        onClick = onIncrement
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onOpenTarget,
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Flag, contentDescription = null, tint = Color(0xFF314E8B))
                            Text("Target", color = Color(0xFF314E8B), fontWeight = FontWeight.SemiBold)
                        }
                    }
                    OutlinedButton(
                        onClick = onOpenInterval,
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = Color(0xFF314E8B))
                            Text("${state.autoIntervalMillis}ms", color = Color(0xFF314E8B), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Loop at target",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.weight(1f))
                    Switch(
                        checked = state.loopAtTarget,
                        onCheckedChange = onLoopChanged
                    )
                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = onToggleAuto,
                        enabled = state.target > 0,
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF7B8296),
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (state.isAutoRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null
                            )
                            Text(
                                text = if (state.isAutoRunning) "Stop Auto" else "Start Auto",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(26.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            TestAdBanner(
                title = "Test Ad : Data Transfer & Phone Clone",
                description = "Quick data sharing app Best file transfer app Move all data",
                ctaText = "Install"
            )
            if (showAutoToast) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = Color(0xFF2E3138)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFD8F3EA)
                        ) {
                            Text(
                                text = "\u2692",
                                modifier = Modifier.padding(8.dp),
                                fontSize = 18.sp,
                                color = Color(0xFF169C86)
                            )
                        }
                        Text(
                            text = "Auto Count Started",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TallyRing(
    count: Int,
    target: Int,
    progress: Float
) {
    Box(
        modifier = Modifier
            .size(540.dp / 2f)
            .drawBehind {
                val strokeWidth = 18.dp.toPx()
                val radius = size.minDimension / 2f - strokeWidth / 2f
                val center = Offset(size.width / 2f, size.height / 2f)
                drawCircle(
                    color = Color(0xFFDDE1ED),
                    radius = radius,
                    center = center,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
                )
                drawArc(
                    color = Color(0xFF314E8B),
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
                    size = Size(size.width - strokeWidth, size.height - strokeWidth),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round
                    )
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 76.sp,
                    lineHeight = 82.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Target: $target",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CounterCircleButton(
    label: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    backgroundColor: Color,
    contentColor: Color,
    size: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.size(size),
        shape = CircleShape,
        color = backgroundColor,
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(34.dp)
                )
            } else if (label != null) {
                Text(
                    text = label,
                    color = contentColor,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun NumberInputDialog(
    title: String,
    label: String,
    initialValue: String,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var input by remember(initialValue) { mutableStateOf(initialValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it.filter(Char::isDigit) },
                label = { Text(label) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = {
            Button(
                onClick = { onSave(input.toIntOrNull() ?: 0) },
                shape = RoundedCornerShape(22.dp)
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun TallyCounterIdlePreview() {
    OfflineToolboxTheme(darkTheme = false) {
        TallyCounterContent(
            state = TallyCounterUiState(count = 1),
            onNavigateBack = {},
            onDecrement = {},
            onIncrement = {},
            onReset = {},
            onOpenTarget = {},
            onOpenInterval = {},
            onLoopChanged = {},
            onToggleAuto = {},
            showAutoToast = false
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TallyCounterAutoPreview() {
    OfflineToolboxTheme(darkTheme = false) {
        TallyCounterContent(
            state = TallyCounterUiState(
                count = 4,
                target = 100,
                autoIntervalMillis = 1000,
                loopAtTarget = true,
                isAutoRunning = true
            ),
            onNavigateBack = {},
            onDecrement = {},
            onIncrement = {},
            onReset = {},
            onOpenTarget = {},
            onOpenInterval = {},
            onLoopChanged = {},
            onToggleAuto = {},
            showAutoToast = true
        )
    }
}
