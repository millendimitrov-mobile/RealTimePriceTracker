package com.milen.realtimepricetracker.ui.feature.feed

import com.milen.realtimepricetracker.domain.model.ConnectionStatus

internal data class FeedState(
    val stocks: List<Any> = emptyList(),
    val connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED,
    val isFeedRunning: Boolean = false,
    val isLoading: Boolean = false,
)

