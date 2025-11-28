package com.milen.realtimepricetracker.domain.logger

import com.milen.realtimepricetracker.BuildConfig

/**
 * Logger interface for centralized logging.
 * Only logs in debug builds for security and performance.
 */
internal interface Logger {
    /**
     * Logs an error event.
     * @param message The error message
     * @param throwable Optional throwable associated with the error
     * @param tag The tag to identify the log source
     */
    fun logError(
        message: String,
        throwable: Throwable? = null,
        tag: String = BuildConfig.APPLICATION_ID,
    )

    /**
     * Logs a general event (debug, info, warning, verbose).
     * @param message The event message
     * @param tag The tag to identify the log source
     */
    fun logEvent(
        message: String,
        tag: String = BuildConfig.APPLICATION_ID,
    )
}
