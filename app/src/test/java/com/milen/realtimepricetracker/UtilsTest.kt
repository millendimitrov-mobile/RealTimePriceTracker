package com.milen.realtimepricetracker

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigDecimal
import java.util.Locale

@RunWith(Parameterized::class)
class FormatPriceParameterizedTest(
    private val price: BigDecimal,
    private val locale: Locale,
    private val expected: String,
) {

    @Test
    fun test() {
        val result = formatPrice(price, locale)
        val normalizedResult = result.replace('\u00A0', ' ')
        assertEquals(expected, normalizedResult)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "price={0}, locale={1}, expected={2}")
        fun data(): Collection<Array<Any>> = listOf(
            arrayOf(BigDecimal("175.50"), Locale.US, "$175.50"),
            arrayOf(BigDecimal("175.50"), Locale.UK, "£175.50"),
            arrayOf(BigDecimal("175.50"), Locale.GERMANY, "175,50 €"),
            arrayOf(BigDecimal("175.50"), Locale.FRANCE, "175,50 €"),
            arrayOf(BigDecimal.ZERO, Locale.US, "$0.00"),
            arrayOf(BigDecimal("999999.99"), Locale.US, "$999,999.99"),
            arrayOf(BigDecimal("0.01"), Locale.US, "$0.01"),
            arrayOf(BigDecimal("-100.50"), Locale.US, "-$100.50"),
            arrayOf(BigDecimal("123.456789"), Locale.US, "$123.46"),
        )
    }
}

@RunWith(Parameterized::class)
class ToLocalizedNoTrailingZerosParameterizedTest(
    private val value: Double,
    private val maxFractionDigits: Int,
    private val locale: Locale,
    private val expected: String,
) {

    @Test
    fun test() {
        val result = value.toLocalizedNoTrailingZeros(maxFractionDigits, locale)
        assertEquals(expected, result)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "value={0}, maxFractionDigits={1}, locale={2}, expected={3}")
        fun data(): Collection<Array<Any>> = listOf(
            arrayOf(123.450, 3, Locale.US, "123.45"),
            arrayOf(123.000, 3, Locale.US, "123"),
            arrayOf(123.100, 3, Locale.US, "123.1"),
            arrayOf(123.0, 3, Locale.US, "123"),
            arrayOf(123.456789, 2, Locale.US, "123.46"),
            arrayOf(123.400, 3, Locale.US, "123.4"),
            arrayOf(0.0, 3, Locale.US, "0"),
            arrayOf(-123.450, 3, Locale.US, "-123.45"),
            arrayOf(0.001, 3, Locale.US, "0.001"),
            arrayOf(0.100, 3, Locale.US, "0.1"),
            arrayOf(123.450, 3, Locale.GERMANY, "123,45"),
            arrayOf(123.450, 3, Locale.FRANCE, "123,45"),
            arrayOf(123.0, 3, Locale.GERMANY, "123"),
            arrayOf(123.456, 0, Locale.US, "123"),
            arrayOf(123.456789, 6, Locale.US, "123.456789"),
            arrayOf(99.000, 3, Locale.US, "99"),
            arrayOf(123.4500, 4, Locale.US, "123.45"),
        )
    }
}

class UtilsSpecialCasesTest {

    @Test
    fun `toLocalizedNoTrailingZeros handles NaN`() {
        val value = Double.NaN
        val result = value.toLocalizedNoTrailingZeros()
        assertEquals("NaN", result)
    }

    @Test
    fun `toLocalizedNoTrailingZeros handles positive infinity`() {
        val value = Double.POSITIVE_INFINITY
        val result = value.toLocalizedNoTrailingZeros()
        assertEquals("Infinity", result)
    }

    @Test
    fun `toLocalizedNoTrailingZeros handles negative infinity`() {
        val value = Double.NEGATIVE_INFINITY
        val result = value.toLocalizedNoTrailingZeros()
        assertEquals("-Infinity", result)
    }
}
