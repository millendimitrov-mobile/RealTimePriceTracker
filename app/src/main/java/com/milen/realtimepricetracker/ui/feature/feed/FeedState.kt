package com.milen.realtimepricetracker.ui.feature.feed

import com.milen.realtimepricetracker.domain.model.ConnectionStatus

internal data class FeedState(
    val connectionStatus: ConnectionStatus = ConnectionStatus.DISCONNECTED,
    val isFeedRunning: Boolean = false,
    val isLoading: Boolean = false,
    val rawMessages: List<String> = emptyList(),
)

