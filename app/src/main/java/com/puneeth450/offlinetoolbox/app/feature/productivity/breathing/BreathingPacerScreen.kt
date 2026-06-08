package com.puneeth450.offlinetoolbox.app.feature.productivity.breathing

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneeth450.offlinetoolbox.app.ui.components.ResultCard
import com.puneeth450.offlinetoolbox.app.ui.components.ToolScaffold

@Composable
fun BreathingPacerScreen(viewModel: BreathingPacerViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val circleScale by animateFloatAsState(
        targetValue = when (state.phase) {
            BreathingPhase.INHALE -> 1.15f
            BreathingPhase.HOLD -> 1.15f
            BreathingPhase.EXHALE -> 0.82f
        },
        animationSpec = spring(),
        label = "breathingScale"
    )

    ToolScaffold("Breathing Pacer") {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .scale(circleScale)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.22f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(state.phase.label, style = MaterialTheme.typography.titleLarge)
            }
        }
        ResultCard("Next change in", "${state.phaseRemainingSeconds} sec")
        ResultCard("Completed cycles", state.cyclesCompleted.toString())
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(onClick = viewModel::toggle) { Text(if (state.isRunning) "Pause" else "Start") }
            Button(onClick = viewModel::reset) { Text("Reset") }
        }
    }
}
