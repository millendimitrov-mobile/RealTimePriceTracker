package com.milen.realtimepricetracker.domain.model

import androidx.compose.runtime.Stable
import java.math.BigDecimal

@Stable
internal data class StockSymbol(
    val id: String,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val previousPrice: BigDecimal? = null,
    val lastUpdate: Long = System.currentTimeMillis(),
) {
    val priceChangeDirection: PriceChangeDirection
        get() = when {
            previousPrice == null -> PriceChangeDirection.UNKNOWN
            price > previousPrice -> PriceChangeDirection.INCREASED
            price < previousPrice -> PriceChangeDirection.DECREASED
            else -> PriceChangeDirection.NO_CHANGE
        }
}

internal enum class PriceChangeDirection {
    INCREASED,
    DECREASED,
    NO_CHANGE,
    UNKNOWN,
}

