package com.puneeth450.offlinetoolbox.app.domain.finance

import java.text.NumberFormat
import java.util.Locale

private val indianLocale = Locale("en", "IN")
private val indianFormat = NumberFormat.getNumberInstance(indianLocale).apply {
    minimumFractionDigits = 0
    maximumFractionDigits = 2
}

fun formatIndianCurrency(value: Double): String {
    return "₹${indianFormat.format(value)}"
}

fun formatIndianCompact(value: Double): String {
    return when {
        value >= 1_00_00_000 -> "₹${"%.2f".format(value / 1_00_00_000)} Cr"
        value >= 1_00_000 -> "₹${"%.2f".format(value / 1_00_000)} L"
        else -> formatIndianCurrency(value)
    }
}
