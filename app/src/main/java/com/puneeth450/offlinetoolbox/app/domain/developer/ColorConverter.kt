package com.puneeth450.offlinetoolbox.app.domain.developer

import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object ColorConverter {
    fun parseHex(hex: String): RgbColor {
        val clean = hex.trim().removePrefix("#")
        require(clean.length == 6) { "HEX must be 6 characters" }
        return RgbColor(
            r = clean.substring(0, 2).toInt(16),
            g = clean.substring(2, 4).toInt(16),
            b = clean.substring(4, 6).toInt(16)
        )
    }

    fun toHex(color: RgbColor): String = "#%02X%02X%02X".format(color.r, color.g, color.b)

    fun toHsl(color: RgbColor): HslColor {
        val r = color.r / 255.0
        val g = color.g / 255.0
        val b = color.b / 255.0
        val max = max(r, max(g, b))
        val min = min(r, min(g, b))
        val lightness = (max + min) / 2
        if (max == min) return HslColor(0, 0, (lightness * 100).roundToInt())
        val delta = max - min
        val saturation = if (lightness > 0.5) delta / (2 - max - min) else delta / (max + min)
        val hue = when (max) {
            r -> ((g - b) / delta + if (g < b) 6 else 0)
            g -> ((b - r) / delta + 2)
            else -> ((r - g) / delta + 4)
        } / 6
        return HslColor((hue * 360).roundToInt(), (saturation * 100).roundToInt(), (lightness * 100).roundToInt())
    }
}

data class RgbColor(val r: Int, val g: Int, val b: Int) {
    init { require(r in 0..255 && g in 0..255 && b in 0..255) }
}
data class HslColor(val h: Int, val s: Int, val l: Int)
