package com.milen.realtimepricetracker.data.config

import com.milen.realtimepricetracker.BuildConfig

internal object WebSocketConfig {
    private const val BASE_URL: String = BuildConfig.WEBSOCKET_BASE_URL
    private const val RAW_PATH = "raw"

    const val RAW_URL: String = "$BASE_URL/$RAW_PATH"
}
