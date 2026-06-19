package com.puneeth450.offlinetoolbox.app.domain.finance

import kotlin.math.pow

object FinanceCalculators {
    fun calculateEmi(principal: Double, annualRate: Double, tenureMonths: Int): EmiResult {
        require(principal > 0) { "Principal must be greater than 0" }
        require(annualRate >= 0) { "Interest rate cannot be negative" }
        require(tenureMonths > 0) { "Tenure must be greater than 0" }

        val monthlyRate = annualRate / 12 / 100
        val emi = if (monthlyRate == 0.0) {
            principal / tenureMonths
        } else {
            principal * monthlyRate * (1 + monthlyRate).pow(tenureMonths) / ((1 + monthlyRate).pow(tenureMonths) - 1)
        }
        val totalPayment = emi * tenureMonths
        return EmiResult(emi, totalPayment - principal, totalPayment)
    }

    fun splitBill(total: Double, people: Int, tipPercent: Double = 0.0, taxPercent: Double = 0.0): SplitBillResult {
        require(total >= 0) { "Total bill cannot be negative" }
        require(people > 0) { "People count must be greater than 0" }
        val tip = total * tipPercent / 100
        val tax = total * taxPercent / 100
        val grandTotal = total + tip + tax
        return SplitBillResult(grandTotal, grandTotal / people, tip, tax)
    }

    fun discount(originalPrice: Double, discountPercent: Double): DiscountResult {
        require(originalPrice >= 0) { "Price cannot be negative" }
        require(discountPercent in 0.0..100.0) { "Discount must be between 0 and 100" }
        val saved = originalPrice * discountPercent / 100
        return DiscountResult(originalPrice - saved, saved)
    }

    fun ruleOf72(rate: Double): Double {
        require(rate > 0) { "Return rate must be greater than 0" }
        return 72 / rate
    }

    fun simpleInterest(principal: Double, annualRate: Double, years: Double): InterestResult {
        require(principal > 0) { "Principal must be greater than 0" }
        require(annualRate >= 0) { "Rate cannot be negative" }
        require(years > 0) { "Time period must be greater than 0" }
        val interest = principal * annualRate * years / 100
        return InterestResult(interest, principal + interest)
    }

    fun compoundInterest(
        principal: Double,
        annualRate: Double,
        years: Double,
        timesPerYear: Int = 1
    ): InterestResult {
        require(principal > 0) { "Principal must be greater than 0" }
        require(annualRate >= 0) { "Rate cannot be negative" }
        require(years > 0) { "Time period must be greater than 0" }
        require(timesPerYear > 0) { "Compounding frequency must be greater than 0" }
        val totalAmount = principal * (1 + annualRate / (100 * timesPerYear)).pow(timesPerYear * years)
        return InterestResult(totalAmount - principal, totalAmount)
    }
}

data class EmiResult(val emi: Double, val totalInterest: Double, val totalPayment: Double)
data class SplitBillResult(val grandTotal: Double, val perPerson: Double, val tipAmount: Double, val taxAmount: Double)
data class DiscountResult(val finalPrice: Double, val savedAmount: Double)
data class InterestResult(val interest: Double, val totalAmount: Double)
