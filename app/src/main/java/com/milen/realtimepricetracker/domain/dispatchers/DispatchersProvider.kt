package com.milen.realtimepricetracker.domain.dispatchers

import kotlinx.coroutines.CoroutineDispatcher

/**
 * Provides coroutine dispatchers for dependency injection.
 * This allows for better testability by enabling test dispatchers in unit tests.
 */
internal interface DispatchersProvider {
    val io: CoroutineDispatcher
    val main: CoroutineDispatcher
    val default: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}
