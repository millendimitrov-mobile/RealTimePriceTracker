package com.milen.realtimepricetracker.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class StockSymbolTest {

    @Test
    fun `priceChangeDirection returns UNKNOWN when previousPrice is null`() {
        val symbol = StockSymbol(
            id = "AAPL",
            name = "Apple",
            description = "Apple Inc.",
            price = BigDecimal("175.50"),
            previousPrice = null
        )

        assertEquals(PriceChangeDirection.UNKNOWN, symbol.priceChangeDirection)
    }

    @Test
    fun `priceChangeDirection returns INCREASED when price is greater than previousPrice`() {
        val symbol = StockSymbol(
            id = "AAPL",
            name = "Apple",
            description = "Apple Inc.",
            price = BigDecimal("180.00"),
            previousPrice = BigDecimal("175.50")
        )

        assertEquals(PriceChangeDirection.INCREASED, symbol.priceChangeDirection)
    }

    @Test
    fun `priceChangeDirection returns DECREASED when price is less than previousPrice`() {
        val symbol = StockSymbol(
            id = "AAPL",
            name = "Apple",
            description = "Apple Inc.",
            price = BigDecimal("170.00"),
            previousPrice = BigDecimal("175.50")
        )

        assertEquals(PriceChangeDirection.DECREASED, symbol.priceChangeDirection)
    }

    @Test
    fun `priceChangeDirection returns NO_CHANGE when price equals previousPrice`() {
        val symbol = StockSymbol(
            id = "AAPL",
            name = "Apple",
            description = "Apple Inc.",
            price = BigDecimal("175.50"),
            previousPrice = BigDecimal("175.50")
        )

        assertEquals(PriceChangeDirection.NO_CHANGE, symbol.priceChangeDirection)
    }

    @Test
    fun `priceChangeDirection handles small price differences correctly`() {
        val symbol = StockSymbol(
            id = "AAPL",
            name = "Apple",
            description = "Apple Inc.",
            price = BigDecimal("175.51"),
            previousPrice = BigDecimal("175.50")
        )

        assertEquals(PriceChangeDirection.INCREASED, symbol.priceChangeDirection)
    }

    @Test
    fun `priceChangeDirection handles large price differences correctly`() {
        val symbol = StockSymbol(
            id = "AAPL",
            name = "Apple",
            description = "Apple Inc.",
            price = BigDecimal("200.00"),
            previousPrice = BigDecimal("100.00")
        )

        assertEquals(PriceChangeDirection.INCREASED, symbol.priceChangeDirection)
    }
}


