package com.milen.realtimepricetracker.ui.feature.details

import com.milen.realtimepricetracker.domain.model.StockSymbol

internal data class SymbolDetailsState(
    val symbol: StockSymbol? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
)

