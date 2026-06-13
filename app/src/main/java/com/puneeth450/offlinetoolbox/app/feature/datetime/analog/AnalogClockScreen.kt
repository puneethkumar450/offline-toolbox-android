package com.puneeth450.offlinetoolbox.app.feature.datetime.analog

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.puneeth450.offlinetoolbox.app.feature.datetime.clock.ClockTimezones
import com.puneeth450.offlinetoolbox.app.feature.datetime.clock.TimezoneDialog
import com.puneeth450.offlinetoolbox.app.ui.components.CommonTopBar
import com.puneeth450.offlinetoolbox.app.ui.components.ScreenPadding
import com.puneeth450.offlinetoolbox.app.ui.components.TestAdBanner
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlinx.coroutines.delay

@Composable
fun AnalogClockScreen(onNavigateBack: () -> Unit) {
    var selectedTimezone by remember { mutableStateOf(ClockTimezones[2]) }
    var showTimezoneDialog by remember { mutableStateOf(false) }
    var nowMillis by remember { mutableStateOf(System.currentTimeMillis()) }

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
        CommonTopBar(
            title = "Analog Clock",
            onNavigateBack = onNavigateBack,
            actionIcon = Icons.Default.Public,
            actionDescription = "Select timezone",
            onActionClick = { showTimezoneDialog = true }
        )

        Spacer(Modifier.height(72.dp))

        AnalogClockFace(
            calendar = rememberClockCalendar(nowMillis, selectedTimezone.zoneId),
            modifier = Modifier
                .fillMaxWidth(0.86f)
                .aspectRatio(1f)
        )

        Spacer(Modifier.height(52.dp))

        Text(
            text = formattedTime(nowMillis, selectedTimezone.zoneId),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = selectedTimezone.label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 10.dp)
        )

        Spacer(Modifier.height(34.dp))
        TestAdBanner(
            title = "Test Ad : Travan: eSIM & Travel SIM",
            description = "Instant Data, No Roaming Enjoy the World Cup without worrying about roaming charges. Get a plan today. Instant",
            ctaText = "Install"
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
private fun AnalogClockFace(calendar: Calendar, modifier: Modifier = Modifier) {
    val faceFill = MaterialTheme.colorScheme.surfaceVariant
    val outerRing = MaterialTheme.colorScheme.outline.copy(alpha = 0.26f)
    val innerRing = MaterialTheme.colorScheme.primary.copy(alpha = 0.30f)
    val majorTick = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.82f)
    val minorTick = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.48f)
    val hand = MaterialTheme.colorScheme.onSurface
    val accent = MaterialTheme.colorScheme.primary
    val centerDot = MaterialTheme.colorScheme.surface

    Canvas(modifier = modifier) {
        val side = min(size.width, size.height)
        val radius = side / 2f
        val center = Offset(size.width / 2f, size.height / 2f)
        val clockRadius = radius * 0.94f

        drawCircle(
            color = outerRing.copy(alpha = 0.22f),
            radius = clockRadius + radius * 0.055f,
            center = center
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    faceFill.copy(alpha = 0.74f),
                    faceFill.copy(alpha = 0.96f)
                ),
                center = center,
                radius = clockRadius
            ),
            radius = clockRadius,
            center = center
        )
        drawCircle(
            color = outerRing,
            radius = clockRadius,
            center = center,
            style = Stroke(width = radius * 0.030f)
        )
        drawCircle(
            color = innerRing,
            radius = clockRadius - radius * 0.026f,
            center = center,
            style = Stroke(width = radius * 0.010f)
        )

        repeat(60) { tick ->
            val isMajor = tick % 5 == 0
            val angle = Math.toRadians((tick * 6 - 90).toDouble())
            val startRadius = clockRadius - if (isMajor) radius * 0.135f else radius * 0.066f
            val endRadius = clockRadius - radius * 0.034f
            val start = center + Offset(
                x = cos(angle).toFloat() * startRadius,
                y = sin(angle).toFloat() * startRadius
            )
            val end = center + Offset(
                x = cos(angle).toFloat() * endRadius,
                y = sin(angle).toFloat() * endRadius
            )
            drawLine(
                color = if (isMajor) majorTick else minorTick,
                start = start,
                end = end,
                strokeWidth = if (isMajor) radius * 0.012f else radius * 0.005f,
                cap = StrokeCap.Round
            )
        }

        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        val hourAngle = ((hour + minute / 60f) * 30f) - 90f
        val minuteAngle = ((minute + second / 60f) * 6f) - 90f
        val secondAngle = second * 6f - 90f

        drawHand(center, hourAngle, clockRadius * 0.47f, hand, radius * 0.048f)
        drawHand(center, minuteAngle, clockRadius * 0.62f, hand, radius * 0.036f)
        drawHand(center, secondAngle, clockRadius * 0.78f, accent, radius * 0.010f)

        drawCircle(color = accent, radius = radius * 0.070f, center = center)
        drawCircle(color = centerDot, radius = radius * 0.030f, center = center)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHand(
    center: Offset,
    degrees: Float,
    length: Float,
    color: Color,
    strokeWidth: Float
) {
    val angle = Math.toRadians(degrees.toDouble())
    val end = center + Offset(
        x = cos(angle).toFloat() * length,
        y = sin(angle).toFloat() * length
    )
    val shadowEnd = end + Offset(3f, 7f)
    drawLine(
        color = Color.Black.copy(alpha = 0.12f),
        start = center + Offset(3f, 7f),
        end = shadowEnd,
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
    drawLine(
        color = color,
        start = center,
        end = end,
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round
    )
}

@Composable
private fun rememberClockCalendar(nowMillis: Long, zoneId: String): Calendar =
    remember(nowMillis, zoneId) {
        Calendar.getInstance(TimeZone.getTimeZone(zoneId)).apply {
            timeInMillis = nowMillis
        }
    }

private fun formattedTime(nowMillis: Long, zoneId: String): String {
    return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone(zoneId)
    }.format(nowMillis)
}
