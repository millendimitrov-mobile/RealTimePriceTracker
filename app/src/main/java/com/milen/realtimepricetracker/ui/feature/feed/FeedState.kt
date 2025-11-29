package com.milen.realtimepricetracker.ui.feature.feed

import com.milen.realtimepricetracker.domain.model.ConnectionStatus
import com.milen.realtimepricetracker.domain.model.StockSymbol

internal data class FeedState(
    val connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED,
    val isFeedRunning: Boolean = false,
    val stocks: List<StockSymbol> = emptyList(),
)

