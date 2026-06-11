package com.puneeth450.offlinetoolbox.app.feature.device.flashlight

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneeth450.offlinetoolbox.app.ui.components.ResultCard
import com.puneeth450.offlinetoolbox.app.ui.components.ToolHeroCard
import com.puneeth450.offlinetoolbox.app.ui.components.ToolMessageCard
import com.puneeth450.offlinetoolbox.app.ui.components.ToolScaffold
import androidx.compose.material3.Icon

@Composable
fun FlashlightScreen(
    onNavigateBack: () -> Unit,
    viewModel: FlashlightViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    ToolScaffold(title = "Flashlight", onNavigateBack = onNavigateBack) {
        ToolHeroCard(
            title = if (state.isEnabled) "Torch is On" else "Torch is Off",
            description = "A quick on-device flashlight toggle with safe hardware checks."
        ) {
            Box(
                modifier = Modifier
                    .size(112.dp)
                    .background(
                        if (state.isEnabled) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.22f)
                        else MaterialTheme.colorScheme.surfaceVariant,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Icon(
                        imageVector = Icons.Default.FlashlightOn,
                        contentDescription = null,
                        tint = if (state.isEnabled) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        ResultCard("Device support", if (state.isSupported) "Flash available" else "Flash unavailable")
        ResultCard("Current state", if (state.isEnabled) "Enabled" else "Disabled")

        if (state.error != null) {
            ToolMessageCard(message = state.error!!, isError = true)
        }

        Button(
            onClick = viewModel::toggleFlashlight,
            enabled = state.isSupported && state.isAvailable && !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (state.isEnabled) "Turn Off" else "Turn On")
        }

        Text(
            "If the button is disabled, another app may be using the camera or the device may not expose torch control.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
