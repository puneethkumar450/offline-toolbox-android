package com.puneeth450.offlinetoolbox.app.feature.datetime.digital

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.puneeth450.offlinetoolbox.app.feature.datetime.clock.ClockTimezone
import com.puneeth450.offlinetoolbox.app.feature.datetime.clock.ClockTimezones
import com.puneeth450.offlinetoolbox.app.feature.datetime.clock.TimezoneDialog
import com.puneeth450.offlinetoolbox.app.ui.components.CommonTopBar
import com.puneeth450.offlinetoolbox.app.ui.components.ScreenPadding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.delay

private val ClockColors = listOf(
    Color.White,
    Color.Black,
    Color(0xFFE0E0E0),
    Color(0xFF4A4A4A),
    Color(0xFFD93035),
    Color(0xFFFF3B30),
    Color(0xFFE57373),
    Color(0xFFFFC1C7),
    Color(0xFFC2185B),
    Color(0xFFE91E63),
    Color(0xFFF06292),
    Color(0xFFF8BBD0),
    Color(0xFF7B1FA2),
    Color(0xFF9C27B0),
    Color(0xFFBA68C8),
    Color(0xFFE1BEE7),
    Color(0xFF1976D2),
    Color(0xFF2196F3),
    Color(0xFF64B5F6),
    Color(0xFFBBDEFB),
    Color(0xFF0097A7),
    Color(0xFF00ACC1),
    Color(0xFF4DD0E1),
    Color(0xFFB2EBF2),
    Color(0xFF388E3C),
    Color(0xFF4CAF50),
    Color(0xFF81C784),
    Color(0xFFC8E6C9),
    Color(0xFFF57C00),
    Color(0xFFFF9800),
    Color(0xFFFFB74D),
    Color(0xFFFFE0B2)
)

@Composable
fun DigitalClockScreen(onNavigateBack: () -> Unit) {
    var selectedTimezone by remember { mutableStateOf(ClockTimezones[2]) }
    var nowMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var is24Hour by remember { mutableStateOf(true) }
    var showSettings by remember { mutableStateOf(false) }
    var showTimezoneDialog by remember { mutableStateOf(false) }
    var customTextColor by remember { mutableStateOf<Color?>(null) }
    val textColor = customTextColor ?: MaterialTheme.colorScheme.onBackground

    LaunchedEffect(selectedTimezone) {
        while (true) {
            nowMillis = System.currentTimeMillis()
            delay(1_000L)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(ScreenPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DigitalClockHeader(
            is24Hour = is24Hour,
            textColor = MaterialTheme.colorScheme.onBackground,
            onNavigateBack = onNavigateBack,
            onToggleFormat = { is24Hour = !is24Hour },
            onSettingsClick = { showSettings = true }
        )

        Spacer(Modifier.height(140.dp))

        DigitalTime(
            nowMillis = nowMillis,
            timezone = selectedTimezone,
            is24Hour = is24Hour,
            textColor = textColor
        )
        Text(
            text = selectedTimezone.label,
            style = MaterialTheme.typography.titleLarge,
            color = textColor.copy(alpha = 0.62f),
            modifier = Modifier.padding(top = 18.dp)
        )
        Spacer(Modifier.height(120.dp))

        Text(
            text = "Rotate device for full-screen mode",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 14.dp)
        )

        DigitalClockAdCard()
    }

    if (showSettings) {
        DigitalClockSettingsDialog(
            selectedTimezone = selectedTimezone,
            textColor = textColor,
            onTextColorSelected = { customTextColor = it },
            onTimezoneClick = { showTimezoneDialog = true },
            onDismiss = { showSettings = false }
        )
    }

    if (showTimezoneDialog) {
        TimezoneDialog(
            selectedTimezone = selectedTimezone,
            onSelect = {
                selectedTimezone = it
                showTimezoneDialog = false
            },
            onDismiss = { showTimezoneDialog = false }
        )
    }
}

@Composable
private fun DigitalClockHeader(
    is24Hour: Boolean,
    textColor: Color,
    onNavigateBack: () -> Unit,
    onToggleFormat: () -> Unit,
    onSettingsClick: () -> Unit
) {
    CommonTopBar(
        title = "Digital Clock",
        onNavigateBack = onNavigateBack,
        contentColor = textColor,
        trailingContent = {
            TextButton(onClick = onToggleFormat) {
                Text(
                    text = if (is24Hour) "24H" else "12H",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "Clock settings", tint = textColor.copy(alpha = 0.78f))
            }
        }
    )
}

@Composable
private fun DigitalTime(
    nowMillis: Long,
    timezone: ClockTimezone,
    is24Hour: Boolean,
    textColor: Color
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = formattedDigitalTime(nowMillis, timezone.zoneId, is24Hour),
            color = textColor,
            fontSize = 58.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 64.sp,
            textAlign = TextAlign.Center
        )
        if (!is24Hour) {
            Text(
                text = formattedMeridiem(nowMillis, timezone.zoneId),
                color = textColor.copy(alpha = 0.78f),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DigitalClockSettingsDialog(
    selectedTimezone: ClockTimezone,
    textColor: Color,
    onTextColorSelected: (Color) -> Unit,
    onTimezoneClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(30.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 6.dp,
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = "Clock Settings",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Normal
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ClockColors.forEach { color ->
                        ColorSwatch(
                            color = color,
                            selected = color == textColor,
                            onClick = { onTextColorSelected(color) }
                        )
                    }
                }
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onTimezoneClick),
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.46f)
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Timezone",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = selectedTimezone.label,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "Done",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorSwatch(color: Color, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(if (color == Color.Black) Color.White else Color.Black.copy(alpha = 0.58f))
            )
        }
    }
}

@Composable
private fun DigitalClockAdCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Surface(
                    shape = RoundedCornerShape(7.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Text(
                        text = "Ad",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                    )
                }
                Spacer(Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFF08BFD2),
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = "Test Ad : Travan: eSIM & Travel SIM",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Instant Data, No Roaming Enjoy the World Cup without worrying about roaming charges. Get a plan today.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = "Install",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            }
        }
    }
}

private fun formattedDigitalTime(nowMillis: Long, zoneId: String, is24Hour: Boolean): String {
    return SimpleDateFormat(if (is24Hour) "HH:mm:ss" else "hh:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone(zoneId)
    }.format(nowMillis)
}

private fun formattedMeridiem(nowMillis: Long, zoneId: String): String {
    return SimpleDateFormat("a", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone(zoneId)
    }.format(nowMillis)
}
