package com.puneeth450.offlinetoolbox.app.feature.datetime.timer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneeth450.offlinetoolbox.app.data.repository.TimerUiState
import com.puneeth450.offlinetoolbox.app.ui.components.CommonTopBar
import com.puneeth450.offlinetoolbox.app.ui.components.ScreenPadding
import com.puneeth450.offlinetoolbox.app.ui.components.TestAdBanner
import com.puneeth450.offlinetoolbox.app.ui.theme.OfflineToolboxTheme

private val TimerPresets = listOf(1, 5, 10, 15, 30, 45, 60, 120)

@Composable
fun TimerScreen(
    onNavigateBack: () -> Unit,
    viewModel: TimerViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showPermissionDialog by remember { mutableStateOf(false) }
    var pendingStartAfterPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && pendingStartAfterPermission) {
            viewModel.startTimer(withNotification = true)
        }
        pendingStartAfterPermission = false
    }

    val canPostNotifications = rememberNotificationPermissionState()

    TimerScreenContent(
        state = state,
        onNavigateBack = onNavigateBack,
        onHoursChanged = viewModel::setHours,
        onMinutesChanged = viewModel::setMinutes,
        onSecondsChanged = viewModel::setSeconds,
        onPresetSelected = viewModel::applyPreset,
        onStartClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !canPostNotifications) {
                showPermissionDialog = true
            } else {
                viewModel.startTimer(withNotification = true)
            }
        },
        onPauseClick = viewModel::pauseTimer,
        onResetClick = viewModel::resetTimer
    )

    if (showPermissionDialog) {
        NotificationPermissionDialog(
            onAllow = {
                showPermissionDialog = false
                pendingStartAfterPermission = true
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            },
            onContinueWithout = {
                showPermissionDialog = false
                pendingStartAfterPermission = false
                viewModel.startTimer(withNotification = false)
            }
        )
    }
}

@Composable
private fun rememberNotificationPermissionState(): Boolean {
    val context = androidx.compose.ui.platform.LocalContext.current
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

@Composable
private fun TimerScreenContent(
    state: TimerUiState,
    onNavigateBack: () -> Unit,
    onHoursChanged: (Int) -> Unit,
    onMinutesChanged: (Int) -> Unit,
    onSecondsChanged: (Int) -> Unit,
    onPresetSelected: (Int) -> Unit,
    onStartClick: () -> Unit,
    onPauseClick: () -> Unit,
    onResetClick: () -> Unit
) {
    val showRunningLayout = state.isRunning || state.remainingMillis != state.totalDurationMillis

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(ScreenPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CommonTopBar(title = "Timer", onNavigateBack = onNavigateBack)

        Spacer(Modifier.height(56.dp))

        if (showRunningLayout) {
            RunningTimerCard(remainingMillis = state.remainingMillis)

            Spacer(Modifier.height(28.dp))

            Text(
                text = "Remaining",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(30.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimerCircleButton(
                    icon = Icons.Default.Refresh,
                    backgroundColor = MaterialTheme.colorScheme.error.copy(alpha = 0.16f),
                    iconColor = MaterialTheme.colorScheme.error.copy(alpha = 0.88f),
                    size = 96.dp,
                    onClick = onResetClick
                )
                TimerCircleButton(
                    icon = if (state.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = if (state.isRunning) 0.34f else 1f),
                    iconColor = if (state.isRunning) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary,
                    size = 114.dp,
                    onClick = if (state.isRunning) onPauseClick else onStartClick
                )
            }
        } else {
            TimerReadout(state = state)

            Spacer(Modifier.height(26.dp))

            TimeSlider(
                label = "Hours",
                value = state.hours,
                maxValue = 23,
                onValueChange = onHoursChanged
            )

            Spacer(Modifier.height(12.dp))

            TimeSlider(
                label = "Minutes",
                value = state.minutes,
                maxValue = 59,
                onValueChange = onMinutesChanged
            )

            Spacer(Modifier.height(12.dp))

            TimeSlider(
                label = "Seconds",
                value = state.seconds,
                maxValue = 59,
                onValueChange = onSecondsChanged
            )

            Spacer(Modifier.height(28.dp))

            Button(
                onClick = onStartClick,
                shape = RoundedCornerShape(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.fillMaxWidth(0.48f)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null
                    )
                    Text("Start Timer", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(34.dp))
            PresetGrid(onPresetSelected = onPresetSelected)
        }

        Spacer(Modifier.height(34.dp))

        TestAdBanner(
            title = "Test Ad : Data Transfer & Phone Clone",
            description = "Quick data sharing app Best file transfer app Move all data",
            ctaText = "Install"
        )
    }
}

@Composable
private fun TimerReadout(state: TimerUiState) {
    Text(
        text = formatTimer(state.remainingMillis),
        style = MaterialTheme.typography.headlineLarge.copy(
            fontSize = 64.sp,
            lineHeight = 70.sp,
            fontWeight = FontWeight.Bold
        ),
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(Modifier.height(10.dp))
    Row(
        modifier = Modifier.fillMaxWidth(0.82f),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        listOf("Hours", "Minutes", "Seconds").forEach { label ->
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RunningTimerCard(remainingMillis: Long) {
    Surface(
        modifier = Modifier.fillMaxWidth(0.84f),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.78f)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 34.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = formatTimer(remainingMillis),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 58.sp,
                    lineHeight = 64.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun TimeSlider(
    label: String,
    value: Int,
    maxValue: Int,
    onValueChange: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$label: $value",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..maxValue.toFloat(),
            steps = (maxValue - 1).coerceAtLeast(0),
            colors = androidx.compose.material3.SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                activeTickColor = MaterialTheme.colorScheme.primary,
                inactiveTickColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
            )
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PresetGrid(onPresetSelected: (Int) -> Unit) {
    Text(
        text = "Quick Presets",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )
    Spacer(Modifier.height(18.dp))
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        TimerPresets.forEach { presetMinutes ->
            val label = if (presetMinutes < 60) {
                "$presetMinutes min"
            } else if (presetMinutes == 60) {
                "1 hour"
            } else {
                "${presetMinutes / 60} hours"
            }
            OutlinedButton(
                onClick = { onPresetSelected(presetMinutes) },
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun TimerCircleButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundColor: Color,
    iconColor: Color,
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
            androidx.compose.material3.Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(44.dp)
            )
        }
    }
}

@Composable
private fun NotificationPermissionDialog(
    onAllow: () -> Unit,
    onContinueWithout: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onContinueWithout) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(26.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Notification Permission\nNeeded",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Notification permission is required for the timer to work in the background and notify you when it finishes.\n\nWithout permission, the timer will only work while the app is open.",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = onAllow,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "Allow Permission",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                OutlinedButton(
                    onClick = onContinueWithout,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = "Continue Without",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

internal fun formatTimer(millis: Long): String {
    val totalSeconds = (millis / 1000L).coerceAtLeast(0L)
    val hours = totalSeconds / 3600L
    val minutes = (totalSeconds % 3600L) / 60L
    val seconds = totalSeconds % 60L
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}

@Preview(showBackground = true)
@Composable
private fun TimerSetupPreview() {
    OfflineToolboxTheme(darkTheme = false) {
        TimerScreenContent(
            state = TimerUiState(),
            onNavigateBack = {},
            onHoursChanged = {},
            onMinutesChanged = {},
            onSecondsChanged = {},
            onPresetSelected = {},
            onStartClick = {},
            onPauseClick = {},
            onResetClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TimerRunningPreview() {
    OfflineToolboxTheme(darkTheme = false) {
        TimerScreenContent(
            state = TimerUiState(
                minutes = 5,
                totalDurationMillis = 5 * 60_000L,
                remainingMillis = 4 * 60_000L + 56_000L,
                isRunning = true
            ),
            onNavigateBack = {},
            onHoursChanged = {},
            onMinutesChanged = {},
            onSecondsChanged = {},
            onPresetSelected = {},
            onStartClick = {},
            onPauseClick = {},
            onResetClick = {}
        )
    }
}
