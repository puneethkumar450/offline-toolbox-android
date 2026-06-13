package com.puneeth450.offlinetoolbox.app.feature.productivity.stopwatch

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
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneeth450.offlinetoolbox.app.ui.components.CommonTopBar
import com.puneeth450.offlinetoolbox.app.ui.components.ScreenPadding
import com.puneeth450.offlinetoolbox.app.ui.components.TestAdBanner
import com.puneeth450.offlinetoolbox.app.ui.theme.OfflineToolboxTheme

@Composable
fun StopwatchTimerScreen(
    onNavigateBack: () -> Unit,
    viewModel: StopwatchTimerViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    StopwatchTimerContent(
        state = state,
        onNavigateBack = onNavigateBack,
        onToggle = viewModel::toggle,
        onReset = viewModel::reset,
        onLap = viewModel::recordLap
    )
}

@Composable
private fun StopwatchTimerContent(
    state: StopwatchTimerUiState,
    onNavigateBack: () -> Unit,
    onToggle: () -> Unit,
    onReset: () -> Unit,
    onLap: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(ScreenPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CommonTopBar(
            title = "Stopwatch",
            onNavigateBack = onNavigateBack
        )

        Spacer(Modifier.height(72.dp))

        StopwatchReadout(
            elapsedMillis = state.elapsedMillis,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(44.dp))

        StopwatchControls(
            isRunning = state.isRunning,
            hasElapsed = state.elapsedMillis > 0L,
            onToggle = onToggle,
            onReset = onReset,
            onLap = onLap
        )

        if (state.laps.isNotEmpty()) {
            Spacer(Modifier.height(58.dp))
            LapSection(laps = state.laps)
        } else {
            Spacer(Modifier.height(82.dp))
        }

        Spacer(Modifier.height(42.dp))
        TestAdBanner(
            title = "Test Ad : Autonomously Return to Home",
            description = "Recall your drone with a push of a button.",
            ctaText = "Open"
        )
    }
}

@Composable
private fun StopwatchReadout(
    elapsedMillis: Long,
    modifier: Modifier = Modifier
) {
    val formatted = formatStopwatchDisplay(elapsedMillis)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = formatted.main,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 56.sp,
                lineHeight = 62.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = formatted.hundredths,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 34.sp,
                lineHeight = 42.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.Monospace
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 2.dp, bottom = 4.dp)
        )
    }
}

@Composable
private fun StopwatchControls(
    isRunning: Boolean,
    hasElapsed: Boolean,
    onToggle: () -> Unit,
    onReset: () -> Unit,
    onLap: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        if (isRunning) {
            StopwatchActionButton(
                icon = Icons.Default.Flag,
                contentDescription = "Add lap",
                containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                contentColor = MaterialTheme.colorScheme.surface,
                size = 84.dp,
                iconSize = 32.dp,
                onClick = onLap
            )
            StopwatchActionButton(
                icon = Icons.Default.Pause,
                contentDescription = "Pause stopwatch",
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
                size = 88.dp,
                iconSize = 38.dp,
                onClick = onToggle
            )
        } else {
            if (hasElapsed) {
                StopwatchActionButton(
                    icon = Icons.Default.Refresh,
                    contentDescription = "Reset stopwatch",
                    containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.16f),
                    contentColor = MaterialTheme.colorScheme.error.copy(alpha = 0.88f),
                    size = 84.dp,
                    iconSize = 34.dp,
                    onClick = onReset
                )
            }
            StopwatchActionButton(
                icon = Icons.Default.PlayArrow,
                contentDescription = "Start stopwatch",
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                size = 88.dp,
                iconSize = 44.dp,
                onClick = onToggle
            )
        }
    }
}

@Composable
private fun StopwatchActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    containerColor: Color,
    contentColor: Color,
    size: androidx.compose.ui.unit.Dp,
    iconSize: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.size(size),
        shape = CircleShape,
        color = containerColor,
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = contentColor,
                modifier = Modifier.size(iconSize)
            )
        }
    }
}

@Composable
private fun LapSection(laps: List<StopwatchLap>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Laps",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        laps.forEach { lap ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Lap ${lap.index}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.82f),
                        modifier = Modifier.weight(1f)
                    )
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = formatLapTime(lap.totalMillis),
                            style = MaterialTheme.typography.headlineSmall.copy(fontFamily = FontFamily.Monospace),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "+${formatLapTime(lap.deltaMillis)}",
                            style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

private data class StopwatchDisplayParts(
    val main: String,
    val hundredths: String
)

private fun formatStopwatchDisplay(millis: Long): StopwatchDisplayParts {
    val totalHundredths = millis / 10
    val hours = totalHundredths / 360_000
    val minutes = (totalHundredths % 360_000) / 6_000
    val seconds = (totalHundredths % 6_000) / 100
    val hundredths = totalHundredths % 100
    return StopwatchDisplayParts(
        main = "%02d:%02d:%02d".format(hours, minutes, seconds),
        hundredths = ":%02d".format(hundredths)
    )
}

private fun formatLapTime(millis: Long): String {
    val totalHundredths = millis / 10
    val hours = totalHundredths / 360_000
    val minutes = (totalHundredths % 360_000) / 6_000
    val seconds = (totalHundredths % 6_000) / 100
    val hundredths = totalHundredths % 100
    return "%02d:%02d.%02d".format(hours * 60 + minutes, seconds, hundredths)
}

@Preview(showBackground = true)
@Composable
private fun StopwatchIdlePreview() {
    OfflineToolboxTheme(darkTheme = false) {
        StopwatchTimerContent(
            state = StopwatchTimerUiState(),
            onNavigateBack = {},
            onToggle = {},
            onReset = {},
            onLap = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StopwatchLapsPreview() {
    OfflineToolboxTheme(darkTheme = false) {
        StopwatchTimerContent(
            state = StopwatchTimerUiState(
                elapsedMillis = 28_570L,
                isRunning = false,
                laps = listOf(
                    StopwatchLap(index = 3, totalMillis = 16_830L, deltaMillis = 500L),
                    StopwatchLap(index = 2, totalMillis = 16_330L, deltaMillis = 1_160L),
                    StopwatchLap(index = 1, totalMillis = 15_170L, deltaMillis = 15_170L)
                )
            ),
            onNavigateBack = {},
            onToggle = {},
            onReset = {},
            onLap = {}
        )
    }
}
