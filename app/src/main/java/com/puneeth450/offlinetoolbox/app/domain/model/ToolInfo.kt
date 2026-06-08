package com.puneeth450.offlinetoolbox.app.domain.model

data class ToolInfo(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: ToolCategory,
    val route: String
)

object ToolCatalog {
    val all = listOf(
        ToolInfo("pomodoro", "Pomodoro Timer", "Focus and break cycles", ToolCategory.PRODUCTIVITY, "pomodoro"),
        ToolInfo("stopwatch_timer", "Stopwatch & Timer", "Count up and count down", ToolCategory.PRODUCTIVITY, "stopwatch_timer"),
        ToolInfo("breathing", "Breathing Pacer", "Guided breathing animation", ToolCategory.PRODUCTIVITY, "breathing"),
        ToolInfo("tally", "Tally Counter", "Count anything locally", ToolCategory.PRODUCTIVITY, "tally"),
        ToolInfo("emi", "EMI Calculator", "Loan EMI and total interest", ToolCategory.FINANCE, "emi"),
        ToolInfo("split_bill", "Split Bill Calculator", "Share bills fairly", ToolCategory.FINANCE, "split_bill"),
        ToolInfo("discount", "Discount Calculator", "Final price after discount", ToolCategory.FINANCE, "discount"),
        ToolInfo("rule72", "Rule of 72 Calculator", "Estimate doubling time", ToolCategory.FINANCE, "rule72"),
        ToolInfo("json", "JSON Formatter", "Format, minify and validate JSON", ToolCategory.DEVELOPER, "json"),
        ToolInfo("url_codec", "URL Encoder/Decoder", "Encode or decode URL text", ToolCategory.DEVELOPER, "url_codec"),
        ToolInfo("hash", "Hash Generator", "MD5, SHA-1, SHA-256, SHA-512", ToolCategory.DEVELOPER, "hash"),
        ToolInfo("lorem", "Lorem Ipsum Generator", "Generate placeholder text", ToolCategory.DEVELOPER, "lorem"),
        ToolInfo("color", "Color Converter", "HEX, RGB and HSL", ToolCategory.DEVELOPER, "color")
    )
}
