package com.milen.realtimepricetracker.ui.feature.feed

internal sealed interface FeedEvent {
    data class NavigateToSymbolDetails(val symbol: String) : FeedEvent
}
