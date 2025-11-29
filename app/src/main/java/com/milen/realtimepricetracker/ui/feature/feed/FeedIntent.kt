package com.milen.realtimepricetracker.ui.feature.feed

internal sealed interface FeedIntent {
    object StartFeed : FeedIntent
    object StopFeed : FeedIntent
    object ToggleFeed : FeedIntent
    data class SymbolClicked(val symbol: String) : FeedIntent
    object Retry : FeedIntent
    object ClearError : FeedIntent
}

