package com.puneeth450.offlinetoolbox.app.domain.model

data class ToolInfo(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: ToolCategory,
    val route: String,
    val keywords: List<String> = emptyList(),
    val featured: Boolean = false
)

object ToolCatalog {
    val all = listOf(
        ToolInfo("analog_clock", "Analog Clock", "Classic clock face for quick time checks", ToolCategory.DATE_TIME, "analog_clock", listOf("clock", "time")),
        ToolInfo("digital_clock", "Digital Clock", "Large numeric time display", ToolCategory.DATE_TIME, "digital_clock", listOf("clock", "time", "digital")),
        ToolInfo("time_zone_converter", "Time Zone Converter", "Compare times across cities and zones", ToolCategory.DATE_TIME, "time_zone_converter", listOf("timezone", "world clock", "convert")),
        ToolInfo("calendar", "Calendar", "Browse dates and plan around days", ToolCategory.DATE_TIME, "calendar", listOf("date", "month")),
        ToolInfo("stopwatch", "Stopwatch", "Count up with simple lap timing", ToolCategory.DATE_TIME, "stopwatch_timer", listOf("stopwatch", "count up"), true),
        ToolInfo("timer", "Timer", "Run a quick countdown", ToolCategory.DATE_TIME, "timer", listOf("timer", "countdown"), true),
        ToolInfo("pomodoro", "Pomodoro Timer", "Focus with calm work and break cycles", ToolCategory.DATE_TIME, "pomodoro", listOf("focus", "study", "timer"), true),
        ToolInfo("tally", "Tally Counter", "Count reps, people or inventory locally", ToolCategory.DATE_TIME, "tally", listOf("counter", "volume keys")),
        ToolInfo("breathing", "Breathing Pacer", "Guided inhale, hold and exhale rhythm", ToolCategory.HEALTH, "breathing", listOf("calm", "wellness", "breath"), true),
        ToolInfo("emi", "EMI Calculator", "Loan EMI, interest and total payment", ToolCategory.FINANCE, "emi", listOf("loan", "interest"), true),
        ToolInfo("split_bill", "Split Bill Calculator", "Share bills with tax and tip", ToolCategory.FINANCE, "split_bill", listOf("tip", "restaurant")),
        ToolInfo("discount", "Discount Calculator", "Final price after discount savings", ToolCategory.FINANCE, "discount", listOf("sale", "price")),
        ToolInfo("rule72", "Rule of 72 Calculator", "Estimate investment doubling time", ToolCategory.FINANCE, "rule72", listOf("investment", "growth")),
        ToolInfo("json", "JSON Formatter", "Format, minify and validate JSON", ToolCategory.DEVELOPER, "json", listOf("api", "pretty print"), true),
        ToolInfo("url_codec", "URL Encoder/Decoder", "Encode or decode URL text", ToolCategory.SOCIAL_WEB, "url_codec", listOf("encode", "decode")),
        ToolInfo("hash", "Hash Generator", "MD5, SHA-1, SHA-256 and SHA-512", ToolCategory.DEVELOPER, "hash", listOf("checksum", "security")),
        ToolInfo("lorem", "Lorem Ipsum Generator", "Generate placeholder text blocks", ToolCategory.TEXT_TOOLS, "lorem", listOf("copy", "placeholder")),
        ToolInfo("color", "Color Converter", "HEX, RGB and HSL conversion", ToolCategory.DEVELOPER, "color", listOf("design", "hex", "rgb")),
        ToolInfo("flashlight", "Flashlight", "Quick torch access with hardware checks", ToolCategory.ESSENTIAL, "flashlight", listOf("torch", "light"), true),
        ToolInfo("device_info", "Device Information", "Key hardware and Android build details", ToolCategory.DEVICE_INFO, "device_info", listOf("model", "android", "system"), true),
        ToolInfo("unit_converter", "Unit Converter", "Length, weight and temperature units", ToolCategory.MEASUREMENT, "unit_converter", listOf("measurement", "convert"), true)
    )
}
