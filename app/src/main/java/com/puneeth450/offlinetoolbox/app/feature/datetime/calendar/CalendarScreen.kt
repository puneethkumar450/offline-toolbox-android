package com.puneeth450.offlinetoolbox.app.feature.datetime.calendar

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.puneeth450.offlinetoolbox.app.feature.datetime.clock.ClockTimezone
import com.puneeth450.offlinetoolbox.app.feature.datetime.clock.ClockTimezones
import com.puneeth450.offlinetoolbox.app.feature.datetime.clock.TimezoneDialog
import com.puneeth450.offlinetoolbox.app.ui.components.CommonTopBar
import com.puneeth450.offlinetoolbox.app.ui.components.ScreenPadding
import com.puneeth450.offlinetoolbox.app.ui.components.ToolSectionCard
import com.puneeth450.offlinetoolbox.app.ui.theme.OfflineToolboxTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

private val WeekdayLabels = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

@Composable
fun CalendarScreen(onNavigateBack: () -> Unit) {
    var selectedTimezone by remember { mutableStateOf(ClockTimezones[2]) }
    var showTimezoneDialog by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(currentCalendar(selectedTimezone.zoneId)) }
    var displayMonth by remember { mutableStateOf(monthAnchor(selectedDate)) }

    LaunchedEffect(selectedTimezone) {
        val today = currentCalendar(selectedTimezone.zoneId)
        selectedDate = today
        displayMonth = monthAnchor(today)
    }

    val monthCells = remember(displayMonth.timeInMillis, selectedDate.timeInMillis, selectedTimezone.zoneId) {
        buildMonthCells(displayMonth, selectedDate)
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
            title = "Calendar",
            onNavigateBack = onNavigateBack,
            actionIcon = Icons.Default.Public,
            actionDescription = "Select timezone",
            onActionClick = { showTimezoneDialog = true }
        )

        Spacer(Modifier.height(40.dp))

        Text(
            text = selectedTimezone.label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { displayMonth = shiftMonth(displayMonth, -1) }) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Previous month",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = monthTitle(displayMonth),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { displayMonth = shiftMonth(displayMonth, 1) }) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Next month",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        CalendarWeekHeader()
        Spacer(Modifier.height(12.dp))
        CalendarMonthGrid(
            monthCells = monthCells,
            onSelectDate = {
                selectedDate = it
                displayMonth = monthAnchor(it)
            }
        )

        Spacer(Modifier.height(28.dp))

        ToolSectionCard(
            title = formatSelectedDay(selectedDate),
            subtitle = "Selected day in ${selectedTimezone.label}"
        ) {
            Text(
                text = formatSelectedDetails(selectedDate),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            Button(
                onClick = {
                    val today = currentCalendar(selectedTimezone.zoneId)
                    selectedDate = today
                    displayMonth = monthAnchor(today)
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Jump to today")
            }
        }
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
private fun CalendarWeekHeader() {
    Row(modifier = Modifier.fillMaxWidth()) {
        WeekdayLabels.forEach { day ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CalendarMonthGrid(
    monthCells: List<CalendarDayCell?>,
    onSelectDate: (Calendar) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        monthCells.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                week.forEach { cell ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (cell != null) {
                            CalendarDay(
                                day = cell.dayOfMonth,
                                isSelected = cell.isSelected,
                                isToday = cell.isToday,
                                onClick = { onSelectDate(cell.date) }
                            )
                        } else {
                            Spacer(Modifier.height(52.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDay(
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val highlightColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.82f)
    val todayOutline = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)

    Surface(
        modifier = Modifier
            .size(52.dp)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = if (isSelected) highlightColor else MaterialTheme.colorScheme.background,
        tonalElevation = if (isSelected) 2.dp else 0.dp,
        border = if (!isSelected && isToday) {
            androidx.compose.foundation.BorderStroke(1.5.dp, todayOutline)
        } else {
            null
        }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onBackground
                },
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

private data class CalendarDayCell(
    val date: Calendar,
    val dayOfMonth: Int,
    val isSelected: Boolean,
    val isToday: Boolean
)

private fun buildMonthCells(displayMonth: Calendar, selectedDate: Calendar): List<CalendarDayCell?> {
    val monthStart = monthAnchor(displayMonth)
    val daysInMonth = monthStart.getActualMaximum(Calendar.DAY_OF_MONTH)
    val offset = monthStart.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY
    val today = currentCalendar(displayMonth.timeZone.id)
    val cells = MutableList<CalendarDayCell?>(offset) { null }

    for (day in 1..daysInMonth) {
        val itemDate = (monthStart.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, day)
        }
        cells += CalendarDayCell(
            date = itemDate,
            dayOfMonth = day,
            isSelected = isSameDay(itemDate, selectedDate),
            isToday = isSameDay(itemDate, today)
        )
    }

    while (cells.size % 7 != 0) {
        cells += null
    }

    return cells
}

private fun currentCalendar(zoneId: String): Calendar =
    Calendar.getInstance(TimeZone.getTimeZone(zoneId)).apply {
        timeInMillis = System.currentTimeMillis()
    }

private fun monthAnchor(calendar: Calendar): Calendar =
    (calendar.clone() as Calendar).apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

private fun shiftMonth(calendar: Calendar, delta: Int): Calendar =
    (calendar.clone() as Calendar).apply {
        add(Calendar.MONTH, delta)
        set(Calendar.DAY_OF_MONTH, 1)
    }

private fun isSameDay(first: Calendar, second: Calendar): Boolean =
    first.get(Calendar.YEAR) == second.get(Calendar.YEAR) &&
        first.get(Calendar.DAY_OF_YEAR) == second.get(Calendar.DAY_OF_YEAR) &&
        first.timeZone.id == second.timeZone.id

private fun monthTitle(calendar: Calendar): String =
    SimpleDateFormat("MMMM yyyy", Locale.getDefault()).apply {
        timeZone = calendar.timeZone
    }.format(calendar.time)

private fun formatSelectedDay(calendar: Calendar): String =
    SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault()).apply {
        timeZone = calendar.timeZone
    }.format(calendar.time)

private fun formatSelectedDetails(calendar: Calendar): String =
    SimpleDateFormat("MMM d, yyyy • z", Locale.getDefault()).apply {
        timeZone = calendar.timeZone
    }.format(calendar.time)

@Preview(showBackground = true)
@Composable
private fun CalendarScreenPreview() {
    OfflineToolboxTheme(darkTheme = false) {
        CalendarScreen(onNavigateBack = {})
    }
}
