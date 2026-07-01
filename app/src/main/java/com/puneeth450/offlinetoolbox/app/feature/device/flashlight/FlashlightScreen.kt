package com.puneeth450.offlinetoolbox.app.feature.device.flashlight

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.puneeth450.offlinetoolbox.app.ui.components.CommonTopBar
import com.puneeth450.offlinetoolbox.app.ui.components.ScreenPadding

@Composable
fun FlashlightScreen(
    onNavigateBack: () -> Unit,
    viewModel: FlashlightViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    if (state.flashMode == FlashMode.Screen && (state.screenLightOn || state.screenStrobeOn)) {
        ScreenFlashView(
            isLightOn = state.screenLightOn,
            onTurnOff = viewModel::toggleFlashlight
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        CommonTopBar(
            title = "Flashlight",
            onNavigateBack = onNavigateBack
        )

        FlashlightPowerControl(
            state = state,
            onClick = viewModel::toggleFlashlight
        )

        FlashModeSection(
            selectedMode = state.flashMode,
            onModeSelected = viewModel::setFlashMode
        )

        StrobeSection(
            state = state,
            onRearStrobeClick = viewModel::toggleRearStrobe,
            onScreenStrobeClick = viewModel::toggleScreenStrobe,
            onSpeedChange = viewModel::setStrobeSpeed
        )

        WarningCard()

        state.error?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun FlashlightPowerControl(
    state: FlashlightUiState,
    onClick: () -> Unit
) {
    val rearFlashOn = state.flashMode == FlashMode.Rear && state.isEnabled
    val circleColor = if (rearFlashOn) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val icon = if (rearFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff
    val iconTint = if (rearFlashOn) {
        MaterialTheme.colorScheme.onTertiary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val statusText = when {
        rearFlashOn -> "REAR FLASH"
        state.rearStrobeOn -> "REAR STROBE"
        state.screenStrobeOn -> "SCREEN STROBE"
        else -> "OFF"
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Surface(
            modifier = Modifier
                .size(184.dp)
                .clip(CircleShape)
                .clickable(enabled = state.isSupported && state.isAvailable && !state.isLoading) { onClick() },
            shape = CircleShape,
            color = circleColor
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (state.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = statusText,
                        modifier = Modifier.size(72.dp),
                        tint = iconTint
                    )
                }
            }
        }

        Text(
            text = statusText,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun FlashModeSection(
    selectedMode: FlashMode,
    onModeSelected: (FlashMode) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Flash Mode",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FlashModeButton(
                text = "Rear",
                icon = Icons.Default.FlashOn,
                selected = selectedMode == FlashMode.Rear,
                onClick = { onModeSelected(FlashMode.Rear) },
                modifier = Modifier.weight(1f)
            )
            FlashModeButton(
                text = "Screen",
                icon = Icons.Default.PhoneAndroid,
                selected = selectedMode == FlashMode.Screen,
                onClick = { onModeSelected(FlashMode.Screen) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FlashModeButton(
    text: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = if (selected) {
        ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f),
            contentColor = MaterialTheme.colorScheme.surface
        )
    } else {
        ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    }

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(8.dp),
        colors = colors
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StrobeSection(
    state: FlashlightUiState,
    onRearStrobeClick: () -> Unit,
    onScreenStrobeClick: () -> Unit,
    onSpeedChange: (Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = "Strobe Mode",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StrobeButton(
                    text = "Rear Strobe",
                    active = state.rearStrobeOn,
                    onClick = onRearStrobeClick,
                    modifier = Modifier.weight(1f)
                )
                StrobeButton(
                    text = "Screen Strobe",
                    active = state.screenStrobeOn,
                    onClick = onScreenStrobeClick,
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = "Speed: ${state.strobeSpeedHz} Hz",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Slider(
                value = state.strobeSpeedHz.toFloat(),
                onValueChange = { onSpeedChange(it.toInt()) },
                valueRange = FlashlightViewModel.MinStrobeHz.toFloat()..FlashlightViewModel.MaxStrobeHz.toFloat(),
                steps = FlashlightViewModel.MaxStrobeHz - FlashlightViewModel.MinStrobeHz - 1,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    activeTickColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.42f),
                    inactiveTrackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.48f),
                    inactiveTickColor = MaterialTheme.colorScheme.primary
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Slow", color = MaterialTheme.colorScheme.onSurface)
                Text("Fast", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun StrobeButton(
    text: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (active) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.primary
            },
            contentColor = if (active) {
                MaterialTheme.colorScheme.onSecondary
            } else {
                MaterialTheme.colorScheme.onPrimary
            }
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun WarningCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = "Strobe warning: May cause discomfort. Use responsibly.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ScreenFlashView(
    isLightOn: Boolean,
    onTurnOff: () -> Unit
) {
    val background = if (isLightOn) Color.White else Color.Black
    val contentColor = if (isLightOn) Color.Black else Color.White

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .clickable { onTurnOff() },
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.padding(top = 54.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FlashOff,
                contentDescription = "Turn off screen flashlight",
                modifier = Modifier.size(48.dp),
                tint = contentColor
            )
            Text(
                text = "Tap icon to turn off",
                style = MaterialTheme.typography.headlineSmall,
                color = contentColor.copy(alpha = 0.48f)
            )
        }
    }
}
