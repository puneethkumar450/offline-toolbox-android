package com.puneeth450.offlinetoolbox.app.feature.productivity.pomodoro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneeth450.offlinetoolbox.app.ui.components.ResultCard
import com.puneeth450.offlinetoolbox.app.ui.components.ToolScaffold

@Composable
fun PomodoroTimerScreen(
    onNavigateBack: () -> Unit,
    viewModel: PomodoroTimerViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    ToolScaffold(title = "Pomodoro Timer", onNavigateBack = onNavigateBack) {
        ResultCard(
            title = if (state.phase == PomodoroPhase.FOCUS) "Focus Session" else "Break Session",
            value = formatDuration(state.remainingMillis)
        )
        Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surfaceVariant) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DurationAdjuster("Focus", state.focusMinutes, viewModel::adjustFocus)
                DurationAdjuster("Break", state.breakMinutes, viewModel::adjustBreak)
            }
        }
        ResultCard("Completed Focus Sessions", state.completedSessions.toString())
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = viewModel::toggleRunning) {
                Text(if (state.isRunning) "Pause" else "Start")
            }
            Button(onClick = viewModel::reset) {
                Text("Reset")
            }
        }
    }
}

@Composable
private fun DurationAdjuster(label: String, value: Int, onAdjust: (Int) -> Unit) {
    androidx.compose.foundation.layout.Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, modifier = Modifier.fillMaxWidth(), fontWeight = FontWeight.SemiBold)
        Text("$value min", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { onAdjust(-1) }) { Text("-") }
            Button(onClick = { onAdjust(1) }) { Text("+") }
        }
    }
}

private fun formatDuration(millis: Long): String {
    val totalSeconds = millis / 1_000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
