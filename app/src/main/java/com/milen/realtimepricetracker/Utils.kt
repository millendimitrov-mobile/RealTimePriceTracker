package com.milen.realtimepricetracker

import java.math.BigDecimal
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale

internal fun formatPrice(price: BigDecimal, locale: Locale = Locale.US): String {
    val formatter = NumberFormat.getCurrencyInstance(locale)
    return formatter.format(price)
}

internal fun Double.toLocalizedNoTrailingZeros(
    maxFractionDigits: Int = 3,
    locale: Locale = Locale.US,
): String {
    if (isNaN() || isInfinite()) return toString()
    val decimalSeparator = DecimalFormatSymbols.getInstance(locale).decimalSeparator
    val raw = String.format(locale, "%.${maxFractionDigits}f", this)
    val trimmed = raw.trimEnd('0').trimEnd(decimalSeparator)
    return trimmed
}