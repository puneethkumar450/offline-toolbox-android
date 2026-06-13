package com.puneeth450.offlinetoolbox.app.feature.productivity.pomodoro

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneeth450.offlinetoolbox.app.ui.components.CommonTopBar
import com.puneeth450.offlinetoolbox.app.ui.components.ScreenPadding
import com.puneeth450.offlinetoolbox.app.ui.theme.OfflineToolboxTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroTimerScreen(
    onNavigateBack: () -> Unit,
    viewModel: PomodoroTimerViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showSettings by remember { mutableStateOf(false) }

    PomodoroTimerContent(
        state = state,
        onNavigateBack = onNavigateBack,
        onShowSettings = { showSettings = true },
        onToggleRunning = viewModel::toggleRunning,
        onReset = viewModel::reset,
        onSkip = viewModel::skipPhase
    )

    if (showSettings) {
        PomodoroSettingsSheet(
            state = state,
            onDismiss = { showSettings = false },
            onSave = { focusMinutes, breakMinutes, cycles ->
                viewModel.saveSettings(focusMinutes, breakMinutes, cycles)
                showSettings = false
            }
        )
    }
}

@Composable
private fun PomodoroTimerContent(
    state: PomodoroTimerUiState,
    onNavigateBack: () -> Unit,
    onShowSettings: () -> Unit,
    onToggleRunning: () -> Unit,
    onReset: () -> Unit,
    onSkip: () -> Unit
) {
    val accent = if (state.phase == PomodoroPhase.FOCUS) Color(0xFF314E8B) else Color(0xFF4CAF50)
    val accentSoft = if (state.phase == PomodoroPhase.FOCUS) Color(0xFFD8DCEB) else Color(0xFFDDEBDF)
    val phaseEmoji = if (state.phase == PomodoroPhase.FOCUS) "\uD83C\uDF45" else "\u2615"
    val phaseLabel = if (state.phase == PomodoroPhase.FOCUS) "WORK" else "BREAK"
    val progress = rememberProgress(state)
    val completedMinutes = (state.completedSessions * state.focusMinutes)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(ScreenPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CommonTopBar(
            title = "Pomodoro Timer",
            onNavigateBack = onNavigateBack,
            actionIcon = Icons.Default.Settings,
            actionDescription = "Pomodoro settings",
            onActionClick = onShowSettings
        )

        Spacer(Modifier.height(38.dp))

        Surface(
            shape = RoundedCornerShape(22.dp),
            color = accentSoft
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = phaseEmoji, fontSize = 21.sp)
                Text(
                    text = phaseLabel,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = accent
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Cycle ${state.currentCycle} / ${state.totalCycles}",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(34.dp))

        TimerCircle(
            timeText = formatDuration(state.remainingMillis),
            progress = progress,
            accent = accent,
            trackColor = accent.copy(alpha = 0.18f)
        )

        Spacer(Modifier.height(36.dp))

        if (state.isRunning) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleControlButton(
                    icon = Icons.Default.Pause,
                    backgroundColor = accent,
                    iconTint = MaterialTheme.colorScheme.onPrimary,
                    size = 84.dp,
                    onClick = onToggleRunning
                )
                OutlinedButton(
                    onClick = onSkip,
                    shape = RoundedCornerShape(26.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "\u23ED Skip",
                        color = Color(0xFF314E8B),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                OutlinedButton(
                    onClick = onReset,
                    shape = RoundedCornerShape(26.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "\u25A0 Stop",
                        color = Color(0xFFC62828),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        } else {
            CircleControlButton(
                icon = Icons.Default.PlayArrow,
                backgroundColor = accent,
                iconTint = MaterialTheme.colorScheme.onPrimary,
                size = 84.dp,
                onClick = onToggleRunning
            )
        }

        Spacer(Modifier.height(48.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 22.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                Text(
                    text = "Today's Progress",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProgressStat(value = state.completedSessions.toString(), label = "Sessions", accent = accent)
                    ProgressStat(value = completedMinutes.toString(), label = "Minutes", accent = accent)
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = "${state.focusMinutes} min work \u2022 ${state.breakMinutes} min break \u2022 ${state.totalCycles} cycles",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TimerCircle(
    timeText: String,
    progress: Float,
    accent: Color,
    trackColor: Color
) {
    Box(
        modifier = Modifier
            .size(480.dp / 2f)
            .drawBehind {
                val strokeWidth = 11.dp.toPx()
                val radius = size.minDimension / 2f - strokeWidth / 2f
                val center = Offset(size.width / 2f, size.height / 2f)
                drawCircle(
                    color = trackColor,
                    radius = radius,
                    center = center,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
                )
                drawArc(
                    color = accent,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
                    size = androidx.compose.ui.geometry.Size(size.width - strokeWidth, size.height - strokeWidth),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round
                    )
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = timeText,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 48.sp,
                lineHeight = 70.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
private fun CircleControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundColor: Color,
    iconTint: Color,
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(44.dp)
            )
        }
    }
}

@Composable
private fun ProgressStat(value: String, label: String, accent: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = accent
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PomodoroSettingsSheet(
    state: PomodoroTimerUiState,
    onDismiss: () -> Unit,
    onSave: (Int, Int, Int) -> Unit
) {
    var focusMinutes by remember(state.focusMinutes) { mutableIntStateOf(state.focusMinutes) }
    var breakMinutes by remember(state.breakMinutes) { mutableIntStateOf(state.breakMinutes) }
    var totalCycles by remember(state.totalCycles) { mutableIntStateOf(state.totalCycles) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        PomodoroSettingsContent(
            focusMinutes = focusMinutes,
            breakMinutes = breakMinutes,
            totalCycles = totalCycles,
            onFocusMinutesChange = { focusMinutes = it },
            onBreakMinutesChange = { breakMinutes = it },
            onCyclesChange = { totalCycles = it },
            onSave = { onSave(focusMinutes, breakMinutes, totalCycles) }
        )
    }
}

@Composable
private fun PomodoroSettingsContent(
    focusMinutes: Int,
    breakMinutes: Int,
    totalCycles: Int,
    onFocusMinutesChange: (Int) -> Unit,
    onBreakMinutesChange: (Int) -> Unit,
    onCyclesChange: (Int) -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        SettingsSlider(
            label = "Work Duration: $focusMinutes min",
            value = focusMinutes,
            range = 5..90,
            onValueChange = onFocusMinutesChange
        )

        SettingsSlider(
            label = "Break Duration: $breakMinutes min",
            value = breakMinutes,
            range = 1..30,
            onValueChange = onBreakMinutesChange
        )

        SettingsSlider(
            label = "Cycles: $totalCycles",
            value = totalCycles,
            range = 1..8,
            onValueChange = onCyclesChange
        )

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF314E8B))
        ) {
            Text(
                text = "Save Settings",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(Modifier.height(18.dp))
    }
}

@Composable
private fun SettingsSlider(
    label: String,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = (range.last - range.first - 1).coerceAtLeast(0),
            colors = androidx.compose.material3.SliderDefaults.colors(
                thumbColor = Color(0xFF314E8B),
                activeTrackColor = Color(0xFF314E8B),
                inactiveTrackColor = Color(0xFF7D849A),
                activeTickColor = Color(0xFF314E8B),
                inactiveTickColor = Color(0xFF314E8B).copy(alpha = 0.45f)
            )
        )
    }
}

private fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1_000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}

private fun rememberProgress(state: PomodoroTimerUiState): Float {
    val totalPhaseMillis = if (state.phase == PomodoroPhase.FOCUS) {
        state.focusMinutes * 60 * 1000L
    } else {
        state.breakMinutes * 60 * 1000L
    }.coerceAtLeast(1)
    return (1f - (state.remainingMillis.toFloat() / totalPhaseMillis.toFloat())).coerceIn(0f, 1f)
}

@Preview(showBackground = true)
@Composable
private fun PomodoroIdlePreview() {
    OfflineToolboxTheme(darkTheme = false) {
        PomodoroTimerContent(
            state = PomodoroTimerUiState(),
            onNavigateBack = {},
            onShowSettings = {},
            onToggleRunning = {},
            onReset = {},
            onSkip = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PomodoroFocusRunningPreview() {
    OfflineToolboxTheme(darkTheme = false) {
        PomodoroTimerContent(
            state = PomodoroTimerUiState(
                remainingMillis = 24 * 60 * 1000L + 59_000L,
                isRunning = true
            ),
            onNavigateBack = {},
            onShowSettings = {},
            onToggleRunning = {},
            onReset = {},
            onSkip = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PomodoroBreakRunningPreview() {
    OfflineToolboxTheme(darkTheme = false) {
        PomodoroTimerContent(
            state = PomodoroTimerUiState(
                phase = PomodoroPhase.BREAK,
                remainingMillis = 4 * 60 * 1000L + 56_000L,
                isRunning = true
            ),
            onNavigateBack = {},
            onShowSettings = {},
            onToggleRunning = {},
            onReset = {},
            onSkip = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PomodoroSettingsSheetPreview() {
    OfflineToolboxTheme {
        Surface {
            PomodoroSettingsContent(
                focusMinutes = 25,
                breakMinutes = 5,
                totalCycles = 4,
                onFocusMinutesChange = {},
                onBreakMinutesChange = {},
                onCyclesChange = {},
                onSave = {}
            )
        }
    }
}
