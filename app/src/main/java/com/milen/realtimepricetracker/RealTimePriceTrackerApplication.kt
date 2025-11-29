package com.milen.realtimepricetracker

import android.app.Application
import com.milen.realtimepricetracker.data.websocket.PriceFeedService
import com.milen.realtimepricetracker.domain.logger.Logger
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class RealTimePriceTrackerApplication : Application() {

    @Inject
    internal lateinit var priceFeedService: PriceFeedService

    @Inject
    internal lateinit var logger: Logger

    override fun onCreate() {
        super.onCreate()

        if (::priceFeedService.isInitialized) {
            logger.logEvent("Starting PriceFeedService", TAG)
            priceFeedService.start()
        } else {
            logger.logError("PriceFeedService not initialized in onCreate()", null, TAG)
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        if (::priceFeedService.isInitialized) {
            logger.logEvent("Stopping PriceFeedService", TAG)
            priceFeedService.stop()
        }
    }

    companion object {
        private const val TAG = "${BuildConfig.APPLICATION_ID}.RealTimePriceTrackerApplication"
    }
}
