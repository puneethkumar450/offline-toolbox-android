package com.puneeth450.offlinetoolbox.app.feature.productivity.stopwatch

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneeth450.offlinetoolbox.app.ui.components.ResultCard
import com.puneeth450.offlinetoolbox.app.ui.components.ToolScaffold

@Composable
fun StopwatchTimerScreen(viewModel: StopwatchTimerViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    ToolScaffold("Stopwatch & Timer") {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ModePill("Stopwatch", state.mode == StopwatchMode.STOPWATCH) { viewModel.setMode(StopwatchMode.STOPWATCH) }
            ModePill("Countdown", state.mode == StopwatchMode.TIMER) { viewModel.setMode(StopwatchMode.TIMER) }
        }
        if (state.mode == StopwatchMode.TIMER) {
            OutlinedTextField(
                value = state.timerInputMinutes,
                onValueChange = viewModel::onTimerInputChanged,
                label = { Text("Timer minutes") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
        }
        ResultCard(
            title = if (state.mode == StopwatchMode.STOPWATCH) "Elapsed" else "Remaining",
            value = formatTime(state.elapsedMillis)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = viewModel::toggle) { Text(if (state.isRunning) "Pause" else "Start") }
            Button(onClick = viewModel::reset) { Text("Reset") }
        }
    }
}

@Composable
private fun ModePill(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = label,
            modifier = Modifier.fillMaxWidth().then(Modifier),
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) "%02d:%02d:%02d".format(hours, minutes, seconds)
    else "%02d:%02d".format(minutes, seconds)
}
