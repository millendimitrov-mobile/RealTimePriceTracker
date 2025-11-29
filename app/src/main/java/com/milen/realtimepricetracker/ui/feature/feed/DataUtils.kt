package com.milen.realtimepricetracker.ui.feature.feed

import com.milen.realtimepricetracker.domain.model.StockSymbol
import java.math.BigDecimal

internal val stockList = listOf(
    StockSymbol(
        id = "AAPL",
        name = "Apple",
        description = "Apple Inc.",
        price = BigDecimal("175.50"),
        previousPrice = BigDecimal("170.00")
    ),
    StockSymbol(
        id = "MSFT",
        name = "Microsoft",
        description = "Microsoft Corporation",
        price = BigDecimal("420.15"),
        previousPrice = BigDecimal("420.15")
    ),
    StockSymbol(
        id = "TSLA",
        name = "Tesla",
        description = "Tesla Inc.",
        price = BigDecimal("245.80"),
        previousPrice = BigDecimal("250.00")
    )
)