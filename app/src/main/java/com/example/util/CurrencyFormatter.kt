package com.example.util

import com.example.data.model.BusinessProfile
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CurrencyFormatter {
    @Volatile
    var defaultUseThousandsSeparator: Boolean = true

    @Volatile
    var defaultThousandsSeparator: String = "."

    fun syncWithProfile(profile: BusinessProfile) {
        defaultUseThousandsSeparator = profile.useThousandsSeparator
        defaultThousandsSeparator = profile.thousandsSeparator
    }

    fun format(
        amount: Double,
        symbol: String = "$",
        useThousandsSeparator: Boolean = defaultUseThousandsSeparator,
        thousandsSeparator: String = defaultThousandsSeparator
    ): String {
        val groupingChar = if (thousandsSeparator == ",") ',' else '.'
        val decimalChar = if (groupingChar == '.') ',' else '.'

        val symbols = DecimalFormatSymbols(Locale.ROOT).apply {
            groupingSeparator = groupingChar
            decimalSeparator = decimalChar
        }

        val isInteger = (amount % 1.0 == 0.0) || (amount == Math.floor(amount))
        val pattern = if (useThousandsSeparator) {
            if (isInteger) "#,##0" else "#,##0.00"
        } else {
            if (isInteger) "#0" else "#0.00"
        }

        val formatter = DecimalFormat(pattern, symbols).apply {
            isGroupingUsed = useThousandsSeparator
            groupingSize = 3
        }

        val formatted = formatter.format(amount)
        return if (symbol.isBlank()) formatted else "$symbol $formatted"
    }

    fun format(amount: Double, profile: BusinessProfile): String {
        return format(
            amount = amount,
            symbol = profile.currencySymbol,
            useThousandsSeparator = profile.useThousandsSeparator,
            thousandsSeparator = profile.thousandsSeparator
        )
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun formatDateShort(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.forLanguageTag("es-ES"))
        return sdf.format(Date(timestamp))
    }

    fun formatOnlyDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    /**
     * Parses numeric user input safely supporting both commas and periods as decimal separators.
     */
    fun parseInputAmount(input: String): Double {
        if (input.isBlank()) return 0.0
        val clean = input.filter { it.isDigit() || it == '.' || it == ',' }
        val normalized = clean.replace(',', '.')
        return normalized.toDoubleOrNull() ?: 0.0
    }

    /**
     * Formats a raw number for edit inputs without scientific notation or unnecessary trailing zeros
     */
    fun formatForInput(amount: Double): String {
        if (amount <= 0.0) return ""
        return if (amount % 1.0 == 0.0) {
            amount.toLong().toString()
        } else {
            amount.toString()
        }
    }
}

