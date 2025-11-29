package com.milen.realtimepricetracker.ui.feature.details

internal sealed interface SymbolDetailsEvent {
    object NavigateBack : SymbolDetailsEvent
}

