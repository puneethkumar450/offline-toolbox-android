package com.puneeth450.offlinetoolbox.app.domain

import com.puneeth450.offlinetoolbox.app.domain.finance.FinanceCalculators
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

class FinanceCalculatorsTest {
    @Test
    fun emiCalculationReturnsPositiveValues() {
        val result = FinanceCalculators.calculateEmi(100000.0, 10.0, 12)
        assertTrue(result.emi > 0)
        assertTrue(result.totalPayment > 100000.0)
    }

    @Test
    fun ruleOf72CalculatesDoublingYears() {
        assertTrue(abs(FinanceCalculators.ruleOf72(12.0) - 6.0) < 0.01)
    }
}
