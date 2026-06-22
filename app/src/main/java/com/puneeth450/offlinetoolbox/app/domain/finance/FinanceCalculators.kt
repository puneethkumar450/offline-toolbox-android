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

    fun sipReturns(monthlyInvestment: Double, annualRate: Double, years: Int): MutualFundResult {
        require(monthlyInvestment > 0) { "Monthly investment must be greater than 0" }
        require(annualRate >= 0) { "Rate cannot be negative" }
        require(years > 0) { "Time period must be greater than 0" }
        val n = years * 12
        val r = annualRate / 12 / 100
        val totalValue = if (r == 0.0) {
            monthlyInvestment * n
        } else {
            monthlyInvestment * ((1 + r).pow(n) - 1) / r * (1 + r)
        }
        val invested = monthlyInvestment * n
        return MutualFundResult(invested, totalValue - invested, totalValue)
    }

    fun lumpsumReturns(principal: Double, annualRate: Double, years: Int): MutualFundResult {
        require(principal > 0) { "Investment amount must be greater than 0" }
        require(annualRate >= 0) { "Rate cannot be negative" }
        require(years > 0) { "Time period must be greater than 0" }
        val totalValue = principal * (1 + annualRate / 100).pow(years.toDouble())
        return MutualFundResult(principal, totalValue - principal, totalValue)
    }

    fun calculateFd(
        principal: Double,
        annualRate: Double,
        tenureYears: Double,
        compoundingFrequency: Int  // times per year: 4 = quarterly, 1 = yearly
    ): FdRdResult {
        require(principal > 0) { "Principal must be greater than 0" }
        require(annualRate >= 0) { "Rate cannot be negative" }
        require(tenureYears > 0) { "Tenure must be greater than 0" }
        val r = annualRate / 100
        val maturity = principal * (1 + r / compoundingFrequency).pow(compoundingFrequency * tenureYears)
        val interest = maturity - principal
        return FdRdResult(principal, 0.0, interest, maturity)
    }

    fun calculateRd(
        monthlyDeposit: Double,
        annualRate: Double,
        tenureMonths: Int
    ): FdRdResult {
        require(monthlyDeposit > 0) { "Monthly deposit must be greater than 0" }
        require(annualRate >= 0) { "Rate cannot be negative" }
        require(tenureMonths > 0) { "Tenure must be greater than 0" }
        // Quarterly compounding: i = r/(4*100), n = months/3 quarters
        val i = annualRate / (4.0 * 100.0)
        val n = tenureMonths / 3.0
        val maturity = monthlyDeposit * ((1 + i).pow(n) - 1) / (1 - (1 + i).pow(-1.0 / 3.0))
        val totalInvestment = monthlyDeposit * tenureMonths
        val interest = maturity - totalInvestment
        return FdRdResult(0.0, totalInvestment, interest, maturity)
    }

    fun calculateGst(amount: Double, ratePercent: Double, inclusive: Boolean): GstResult {
        require(amount >= 0) { "Amount cannot be negative" }
        require(ratePercent >= 0) { "GST rate cannot be negative" }
        return if (inclusive) {
            val base = amount * 100.0 / (100.0 + ratePercent)
            val gst = amount - base
            GstResult(base, gst, amount)
        } else {
            val gst = amount * ratePercent / 100.0
            GstResult(amount, gst, amount + gst)
        }
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
data class GstResult(val baseAmount: Double, val gstAmount: Double, val totalAmount: Double)
// principal is 0 for RD (use totalInvestment); totalInvestment is 0 for FD (use principal)
data class FdRdResult(val principal: Double, val totalInvestment: Double, val interestEarned: Double, val maturityAmount: Double)
data class MutualFundResult(
    val investedAmount: Double,
    val estimatedReturns: Double,
    val totalValue: Double
)
