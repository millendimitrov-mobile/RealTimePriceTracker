package com.milen.realtimepricetracker.domain.logger

import android.util.Log
import com.milen.realtimepricetracker.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Default implementation of Logger that only logs in debug builds.
 * In release builds, all logging is disabled for security and performance.
 */
@Singleton
internal class AppLogger @Inject constructor() : Logger {

    private val isLoggingEnabled = BuildConfig.DEBUG

    override fun logError(
        message: String,
        throwable: Throwable?,
        tag: String,
    ) {
        if (isLoggingEnabled) {
            if (throwable != null) {
                Log.e(tag, message, throwable)
            } else {
                Log.e(tag, message)
            }
        }
    }

    override fun logEvent(
        message: String,
        tag: String,
    ) {
        if (isLoggingEnabled) {
            Log.d(tag, message)
        }
    }
}
