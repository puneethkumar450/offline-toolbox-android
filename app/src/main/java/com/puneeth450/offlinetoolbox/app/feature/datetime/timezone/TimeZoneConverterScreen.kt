package com.puneeth450.offlinetoolbox.app.feature.datetime.timezone

import android.view.ContextThemeWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.puneeth450.offlinetoolbox.app.R
import com.puneeth450.offlinetoolbox.app.ui.components.CommonTopBar
import com.puneeth450.offlinetoolbox.app.ui.components.ScreenPadding
import android.app.TimePickerDialog as PlatformTimePickerDialog
import android.icu.util.TimeZone as IcuTimeZone
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

private data class TimeZonePlace(
    val name: String,
    val city: String,
    val zoneId: String,
    val flag: String
)

private val Places: List<TimeZonePlace> = buildCountryTimeZonePlaces()

@Composable
fun TimeZoneConverterScreen(onNavigateBack: () -> Unit) {
    var fromPlace by remember { mutableStateOf(Places.firstOrNull { it.zoneId == "Asia/Kolkata" } ?: Places.first()) }
    var toPlace by remember { mutableStateOf(Places.firstOrNull { it.zoneId == "America/New_York" } ?: Places.first()) }
    val initialCalendar = remember {
        Calendar.getInstance(TimeZone.getTimeZone(fromPlace.zoneId))
    }
    var year by remember { mutableIntStateOf(initialCalendar.get(Calendar.YEAR)) }
    var month by remember { mutableIntStateOf(initialCalendar.get(Calendar.MONTH)) }
    var day by remember { mutableIntStateOf(initialCalendar.get(Calendar.DAY_OF_MONTH)) }
    var hour by remember { mutableIntStateOf(initialCalendar.get(Calendar.HOUR_OF_DAY)) }
    var minute by remember { mutableIntStateOf(initialCalendar.get(Calendar.MINUTE)) }
    var selectingFrom by remember { mutableStateOf(true) }
    var showCountryDialog by remember { mutableStateOf(false) }
    var showDateDialog by remember { mutableStateOf(false) }
    var showTimeDialog by remember { mutableStateOf(false) }
    var convertedCalendar by remember { mutableStateOf(convertDateTime(year, month, day, hour, minute, fromPlace, toPlace)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(ScreenPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CommonTopBar(title = "Time Zone Converter", onNavigateBack = onNavigateBack)

        Spacer(Modifier.height(21.dp))

        DateTimeInputCard(
            dateText = formatDate(year, month, day),
            timeText = formatTime(hour, minute),
            onDateClick = { showDateDialog = true },
            onTimeClick = { showTimeDialog = true }
        )

        Spacer(Modifier.height(18.dp))
        PlaceSection(
            title = "From",
            place = fromPlace,
            onClick = {
                selectingFrom = true
                showCountryDialog = true
            }
        )

        Icon(
            imageVector = Icons.Default.SwapHoriz,
            contentDescription = "Swap countries",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .size(24.dp)
                .clickable {
                    val previousFrom = fromPlace
                    fromPlace = toPlace
                    toPlace = previousFrom
                    convertedCalendar = convertDateTime(year, month, day, hour, minute, fromPlace, toPlace)
                }
        )

        PlaceSection(
            title = "To",
            place = toPlace,
            onClick = {
                selectingFrom = false
                showCountryDialog = true
            }
        )

        Spacer(Modifier.height(18.dp))
        Button(
            onClick = {
                convertedCalendar = convertDateTime(year, month, day, hour, minute, fromPlace, toPlace)
            },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = "Convert",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 2.dp)
            )
        }

        ConvertedResultCard(place = toPlace, calendar = convertedCalendar)

        Spacer(Modifier.height(12.dp))
        TimeZoneAdCard()
    }

    if (showCountryDialog) {
        CountryDialog(
            selectedPlace = if (selectingFrom) fromPlace else toPlace,
            onSelect = { place ->
                if (selectingFrom) fromPlace = place else toPlace = place
                convertedCalendar = convertDateTime(year, month, day, hour, minute, fromPlace, toPlace)
                showCountryDialog = false
            },
            onDismiss = { showCountryDialog = false }
        )
    }
    if (showDateDialog) {
        DatePickerDialog(
            year = year,
            month = month,
            day = day,
            onDismiss = { showDateDialog = false },
            onConfirm = { selectedYear, selectedMonth, selectedDay ->
                year = selectedYear
                month = selectedMonth
                day = selectedDay
                convertedCalendar = convertDateTime(year, month, day, hour, minute, fromPlace, toPlace)
                showDateDialog = false
            }
        )
    }
    if (showTimeDialog) {
        TimePickerDialog(
            hour = hour,
            minute = minute,
            onDismiss = { showTimeDialog = false },
            onConfirm = { selectedHour, selectedMinute ->
                hour = selectedHour
                minute = selectedMinute
                convertedCalendar = convertDateTime(year, month, day, hour, minute, fromPlace, toPlace)
                showTimeDialog = false
            }
        )
    }
}

@Composable
private fun DateTimeInputCard(
    dateText: String,
    timeText: String,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Select Date & Time",
                style = MaterialTheme.typography.titleSmall.copy(fontSize = 14.sp),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                DateTimeField(
                    text = dateText,
                    icon = Icons.Default.CalendarToday,
                    onClick = onDateClick,
                    modifier = Modifier.weight(1f)
                )
                DateTimeField(
                    text = timeText,
                    icon = Icons.Default.AccessTime,
                    onClick = onTimeClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DateTimeField(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(42.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.background,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(icon, contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(21.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun PlaceSection(title: String, place: TimeZonePlace, onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))
        PlaceSelector(place = place, onClick = onClick)
    }
}

@Composable
private fun PlaceSelector(place: TimeZonePlace, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.background,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 21.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(place.flag, fontSize = 21.sp)
            Spacer(Modifier.width(18.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = place.city,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }
    }
}

@Composable
private fun ConvertedResultCard(place: TimeZonePlace, calendar: Calendar) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 18.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(place.flag, fontSize = 24.sp)
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${place.name} time",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${formatCalendarTime(calendar)} • ${formatCalendarDate(calendar)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun CountryDialog(
    selectedPlace: TimeZonePlace,
    onSelect: (TimeZonePlace) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filteredPlaces = remember(query) {
        Places.filter {
            query.isBlank() ||
                it.name.contains(query, ignoreCase = true) ||
                it.city.contains(query, ignoreCase = true)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 6.dp,
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 21.dp, vertical = 21.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = "Select Country",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    placeholder = { Text("Search country or city...") }
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(470.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filteredPlaces.forEach { place ->
                        CountryRow(
                            place = place,
                            selected = place == selectedPlace,
                            onClick = { onSelect(place) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CountryRow(place: TimeZonePlace, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.14f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(place.flag, fontSize = 24.sp)
        Spacer(Modifier.width(18.dp))
        Column {
            Text(
                text = place.name,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = place.city,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DatePickerDialog(
    year: Int,
    month: Int,
    day: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int, Int) -> Unit
) {
    var pickerYear by remember { mutableIntStateOf(year) }
    var pickerMonth by remember { mutableIntStateOf(month) }
    var pickerDay by remember { mutableIntStateOf(day) }
    val calendar = remember(pickerYear, pickerMonth) {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, pickerYear)
            set(Calendar.MONTH, pickerMonth)
            set(Calendar.DAY_OF_MONTH, 1)
        }
    }
    val monthLabel = remember(pickerYear, pickerMonth) {
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time)
    }
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOffset = calendar.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 6.dp,
            shadowElevation = 10.dp
        ) {
            Column(modifier = Modifier.padding(28.dp)) {
                Text(
                    text = "Select date",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatLongDate(pickerYear, pickerMonth, pickerDay),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(top = 30.dp, bottom = 34.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = monthLabel,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = "Previous month",
                        modifier = Modifier
                            .size(44.dp)
                            .clickable {
                                if (pickerMonth == 0) {
                                    pickerMonth = 11
                                    pickerYear -= 1
                                } else {
                                    pickerMonth -= 1
                                }
                                pickerDay = pickerDay.coerceAtMost(daysInMonth)
                            }
                            .padding(8.dp)
                    )
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Next month",
                        modifier = Modifier
                            .size(44.dp)
                            .clickable {
                                if (pickerMonth == 11) {
                                    pickerMonth = 0
                                    pickerYear += 1
                                } else {
                                    pickerMonth += 1
                                }
                                pickerDay = pickerDay.coerceAtMost(daysInMonth)
                            }
                            .padding(8.dp)
                    )
                }
                Spacer(Modifier.height(22.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    listOf("S", "M", "T", "W", "T", "F", "S").forEach {
                        Text(
                            text = it,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                FlowRow(
                    maxItemsInEachRow = 7,
                    horizontalArrangement = Arrangement.spacedBy(0.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    repeat(firstDayOffset) {
                        Spacer(Modifier.size(42.dp))
                    }
                    (1..daysInMonth).forEach { date ->
                        DayCell(
                            day = date,
                            selected = date == pickerDay,
                            onClick = { pickerDay = date }
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 36.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", fontWeight = FontWeight.Bold)
                    }
                    TextButton(onClick = { onConfirm(pickerYear, pickerMonth, pickerDay) }) {
                        Text("OK", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(day: Int, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(if (selected) MaterialTheme.colorScheme.primary else Color.Transparent)
            .border(
                width = if (selected) 0.dp else 1.dp,
                color = Color.Transparent,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.titleMedium,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun TimePickerDialog(
    hour: Int,
    minute: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val context = LocalContext.current
    val dialog = remember(hour, minute) {
        val themedContext = ContextThemeWrapper(context, R.style.Theme_OfflineToolbox_TimePickerDialog)
        PlatformTimePickerDialog(
            themedContext,
            { _, selectedHour, selectedMinute -> onConfirm(selectedHour, selectedMinute) },
            hour,
            minute,
            false
        )
    }

    DisposableEffect(dialog) {
        dialog.setOnDismissListener { onDismiss() }
        dialog.show()
        onDispose {
            dialog.setOnDismissListener(null)
            dialog.dismiss()
        }
    }
}

@Composable
private fun TimeZoneAdCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Surface(shape = RoundedCornerShape(7.dp), color = MaterialTheme.colorScheme.surface) {
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
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Text(
                text = "Test Ad : LG ArtCool klima",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Moderan dizajn i snazno hladjenje za maksimalnu udobnost u svakom domu.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun convertDateTime(
    year: Int,
    month: Int,
    day: Int,
    hour: Int,
    minute: Int,
    fromPlace: TimeZonePlace,
    toPlace: TimeZonePlace
): Calendar {
    val source = Calendar.getInstance(TimeZone.getTimeZone(fromPlace.zoneId)).apply {
        clear()
        set(year, month, day, hour, minute, 0)
    }
    return Calendar.getInstance(TimeZone.getTimeZone(toPlace.zoneId)).apply {
        timeInMillis = source.timeInMillis
    }
}

private fun formatDate(year: Int, month: Int, day: Int): String =
    "%02d/%02d/%04d".format(day, month + 1, year)

private fun formatTime(hour: Int, minute: Int): String =
    "%02d:%02d".format(hour, minute)

private fun formatLongDate(year: Int, month: Int, day: Int): String {
    val calendar = Calendar.getInstance().apply {
        set(year, month, day)
    }
    return SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(calendar.time)
}

private fun formatCalendarDate(calendar: Calendar): String =
    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)

private fun formatCalendarTime(calendar: Calendar): String =
    SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)

private fun buildCountryTimeZonePlaces(): List<TimeZonePlace> {
    return Locale.getISOCountries()
        .flatMap { countryCode ->
            val countryName = Locale("", countryCode).displayCountry.takeIf { it.isNotBlank() }
                ?: return@flatMap emptyList()
            val zoneIds = IcuTimeZone.getAvailableIDs(countryCode)
                .filterNot { it == "Etc/Unknown" }
                .distinct()
                .sorted()
            val resolvedZoneIds = zoneIds.ifEmpty { listOf("UTC") }

            resolvedZoneIds.map { zoneId ->
                val city = zoneId.toCityName()
                TimeZonePlace(
                    name = if (resolvedZoneIds.size > 1) "${countryName.toDisplayCountryName()} ($city)" else countryName.toDisplayCountryName(),
                    city = city,
                    zoneId = zoneId,
                    flag = countryCode.toFlagEmoji()
                )
            }
        }
        .sortedWith(compareBy<TimeZonePlace> { it.name }.thenBy { it.city })
}

private fun String.toDisplayCountryName(): String = when (this) {
    "United States" -> "USA"
    "United Kingdom" -> "UK"
    "United Arab Emirates" -> "UAE"
    else -> this
}

private fun String.toCityName(): String {
    return substringAfterLast('/')
        .replace('_', ' ')
        .ifBlank { this }
}

private fun String.toFlagEmoji(): String {
    return uppercase(Locale.US)
        .take(2)
        .map { char -> Character.toChars(0x1F1E6 + (char.code - 'A'.code)).concatToString() }
        .joinToString("")
}
