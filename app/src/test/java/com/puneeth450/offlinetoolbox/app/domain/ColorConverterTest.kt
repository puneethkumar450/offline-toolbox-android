package com.puneeth450.offlinetoolbox.app.domain

import com.puneeth450.offlinetoolbox.app.domain.developer.ColorConverter
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorConverterTest {
    @Test
    fun parsesHexAndReturnsSameHex() {
        val rgb = ColorConverter.parseHex("#336699")
        assertEquals("#336699", ColorConverter.toHex(rgb))
    }
}
