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
        ToolInfo("pomodoro", "Pomodoro Timer", "Focus with calm work and break cycles", ToolCategory.PRODUCTIVITY, "pomodoro", listOf("focus", "study", "timer"), true),
        ToolInfo("stopwatch_timer", "Stopwatch & Timer", "Count up or run a quick countdown", ToolCategory.PRODUCTIVITY, "stopwatch_timer", listOf("countdown", "time"), true),
        ToolInfo("breathing", "Breathing Pacer", "Guided inhale, hold and exhale rhythm", ToolCategory.PRODUCTIVITY, "breathing", listOf("calm", "wellness", "breath"), true),
        ToolInfo("tally", "Tally Counter", "Count reps, people or inventory locally", ToolCategory.PRODUCTIVITY, "tally", listOf("counter", "volume keys")),
        ToolInfo("emi", "EMI Calculator", "Loan EMI, interest and total payment", ToolCategory.FINANCE, "emi", listOf("loan", "interest"), true),
        ToolInfo("split_bill", "Split Bill Calculator", "Share bills with tax and tip", ToolCategory.FINANCE, "split_bill", listOf("tip", "restaurant")),
        ToolInfo("discount", "Discount Calculator", "Final price after discount savings", ToolCategory.FINANCE, "discount", listOf("sale", "price")),
        ToolInfo("rule72", "Rule of 72 Calculator", "Estimate investment doubling time", ToolCategory.FINANCE, "rule72", listOf("investment", "growth")),
        ToolInfo("json", "JSON Formatter", "Format, minify and validate JSON", ToolCategory.DEVELOPER, "json", listOf("api", "pretty print"), true),
        ToolInfo("url_codec", "URL Encoder/Decoder", "Encode or decode URL text", ToolCategory.DEVELOPER, "url_codec", listOf("encode", "decode")),
        ToolInfo("hash", "Hash Generator", "MD5, SHA-1, SHA-256 and SHA-512", ToolCategory.DEVELOPER, "hash", listOf("checksum", "security")),
        ToolInfo("lorem", "Lorem Ipsum Generator", "Generate placeholder text blocks", ToolCategory.DEVELOPER, "lorem", listOf("copy", "placeholder")),
        ToolInfo("color", "Color Converter", "HEX, RGB and HSL conversion", ToolCategory.DEVELOPER, "color", listOf("design", "hex", "rgb")),
        ToolInfo("flashlight", "Flashlight", "Quick torch access with hardware checks", ToolCategory.DEVICE, "flashlight", listOf("torch", "light"), true),
        ToolInfo("device_info", "Device Information", "Key hardware and Android build details", ToolCategory.DEVICE, "device_info", listOf("model", "android", "system"), true),
        ToolInfo("unit_converter", "Unit Converter", "Length, weight and temperature units", ToolCategory.DEVICE, "unit_converter", listOf("measurement", "convert"), true)
    )
}
