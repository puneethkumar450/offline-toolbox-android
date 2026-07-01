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
        ToolInfo("invoice_generator", "Invoice Generator", "Create and share simple invoices", ToolCategory.FINANCE, "invoice_generator", listOf("bill", "receipt", "invoice")),
        ToolInfo("interest_calculator", "Interest Calculator", "Simple and compound interest", ToolCategory.FINANCE, "interest_calculator", listOf("interest", "compound", "simple")),
        ToolInfo("mutual_fund", "Mutual Fund", "Estimate SIP and lumpsum returns", ToolCategory.FINANCE, "mutual_fund", listOf("sip", "investment", "returns")),
        ToolInfo("emi", "EMI Calculator", "Loan EMI, interest and total payment", ToolCategory.FINANCE, "emi", listOf("loan", "interest"), true),
        ToolInfo("gst_calculator", "GST Calculator", "Add or remove GST from amounts", ToolCategory.FINANCE, "gst_calculator", listOf("gst", "tax", "vat")),
        ToolInfo("fd_rd_calculator", "FD & RD Calculator", "Fixed and recurring deposit maturity", ToolCategory.FINANCE, "fd_rd_calculator", listOf("fd", "rd", "deposit", "maturity")),
        ToolInfo("expense_tracker", "Expense Tracker", "Track daily spending offline", ToolCategory.FINANCE, "expense_tracker", listOf("budget", "spending", "money")),
        ToolInfo("gold_silver_rates", "Gold/Silver Rates", "Compute gold and silver value", ToolCategory.FINANCE, "gold_silver_rates", listOf("gold", "silver", "metal", "rate")),
        //ToolInfo("split_bill", "Split Bill Calculator", "Share bills with tax and tip", ToolCategory.FINANCE, "split_bill", listOf("tip", "restaurant")),
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
