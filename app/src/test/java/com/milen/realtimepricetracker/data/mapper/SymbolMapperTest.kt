package com.milen.realtimepricetracker.data.mapper

import com.milen.realtimepricetracker.data.network.model.SymbolDto
import com.milen.realtimepricetracker.domain.model.StockSymbol
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class SymbolMapperTest {

    private lateinit var mapper: SymbolMapper

    @Before
    fun setup() {
        mapper = SymbolMapper()
    }

    @Test
    fun `toDomain converts SymbolDto to StockSymbol without previous price`() {
        val dto = SymbolDto(
            id = "AAPL",
            name = "Apple",
            description = "Apple Inc.",
            price = BigDecimal("175.50")
        )

        val result = mapper.toDomain(dto)

        assertEquals("AAPL", result.id)
        assertEquals("Apple", result.name)
        assertEquals("Apple Inc.", result.description)
        assertEquals(BigDecimal("175.50"), result.price)
        assertNull(result.previousPrice)
    }

    @Test
    fun `toDomain converts SymbolDto to StockSymbol with previous price`() {
        val dto = SymbolDto(
            id = "AAPL",
            name = "Apple",
            description = "Apple Inc.",
            price = BigDecimal("180.00")
        )
        val previousPrice = BigDecimal("175.50")

        val result = mapper.toDomain(dto, previousPrice)

        assertEquals("AAPL", result.id)
        assertEquals(BigDecimal("180.00"), result.price)
        assertEquals(previousPrice, result.previousPrice)
    }

    @Test
    fun `toDomainList converts empty list`() {
        val result = mapper.toDomainList(emptyMap(), emptyList())

        assertEquals(0, result.size)
    }

    @Test
    fun `toDomainList converts list of DTOs without previous stocks`() {
        val dtos = listOf(
            SymbolDto("AAPL", "Apple", "Apple Inc.", BigDecimal("175.50")),
            SymbolDto("GOOGL", "Google", "Google LLC", BigDecimal("150.00"))
        )

        val result = mapper.toDomainList(emptyMap(), dtos)

        assertEquals(2, result.size)
        assertEquals("AAPL", result[0].id)
        assertEquals("GOOGL", result[1].id)
        assertNull(result[0].previousPrice)
        assertNull(result[1].previousPrice)
    }

    @Test
    fun `toDomainList preserves previous prices from previous stocks map`() {
        val previousStocks = mapOf(
            "AAPL" to StockSymbol(
                id = "AAPL",
                name = "Apple",
                description = "Apple Inc.",
                price = BigDecimal("170.00"),
                previousPrice = null
            ),
            "GOOGL" to StockSymbol(
                id = "GOOGL",
                name = "Google",
                description = "Google LLC",
                price = BigDecimal("145.00"),
                previousPrice = null
            )
        )

        val dtos = listOf(
            SymbolDto("AAPL", "Apple", "Apple Inc.", BigDecimal("175.50")),
            SymbolDto("GOOGL", "Google", "Google LLC", BigDecimal("150.00"))
        )

        val result = mapper.toDomainList(previousStocks, dtos)

        assertEquals(2, result.size)
        assertEquals(BigDecimal("170.00"), result.find { it.id == "AAPL" }?.previousPrice)
        assertEquals(BigDecimal("145.00"), result.find { it.id == "GOOGL" }?.previousPrice)
    }

    @Test
    fun `toDomainList handles new stocks not in previous map`() {
        val previousStocks = mapOf(
            "AAPL" to StockSymbol(
                id = "AAPL",
                name = "Apple",
                description = "Apple Inc.",
                price = BigDecimal("170.00"),
                previousPrice = null
            )
        )

        val dtos = listOf(
            SymbolDto("AAPL", "Apple", "Apple Inc.", BigDecimal("175.50")),
            SymbolDto("MSFT", "Microsoft", "Microsoft Corp.", BigDecimal("420.00"))
        )

        val result = mapper.toDomainList(previousStocks, dtos)

        assertEquals(2, result.size)
        assertEquals(BigDecimal("170.00"), result.find { it.id == "AAPL" }?.previousPrice)
        assertNull(result.find { it.id == "MSFT" }?.previousPrice)
    }
}


