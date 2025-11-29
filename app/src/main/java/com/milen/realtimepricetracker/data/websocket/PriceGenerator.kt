package com.milen.realtimepricetracker.data.websocket

import com.milen.realtimepricetracker.BuildConfig
import com.milen.realtimepricetracker.data.network.model.SymbolDto
import com.milen.realtimepricetracker.di.qualifiers.ApplicationScope
import com.milen.realtimepricetracker.domain.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
internal class PriceGenerator @Inject constructor(
    @param:ApplicationScope private val appScope: CoroutineScope,
    private val json: Json,
    private val logger: Logger,
) {


    private var currentStocks: List<SymbolDto> = StockData.INITIAL_STOCKS

    private val _priceUpdates = MutableSharedFlow<String>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
    )
    val priceUpdates: SharedFlow<String> = _priceUpdates.asSharedFlow()

    private val generatingMutex = Mutex()
    private var generatingJob: Job? = null

    fun startGenerating() {
        appScope.launch {
            generatingMutex.withLock {
                if (generatingJob?.isActive == true) {
                    logger.logEvent("PriceGenerator already generating", TAG)
                    return@withLock
                }

                logger.logEvent("Starting price generation loop", TAG)
                generatingJob = appScope.launch {
                    while (true) {
                        updatePrices()
                        val jsonString = json.encodeToString(currentStocks)
                        logger.logEvent(
                            "Emitting price update (${jsonString.length} bytes, ${currentStocks.size} stocks)",
                            TAG
                        )
                        _priceUpdates.emit(jsonString)
                        delay(PRICE_UPDATE_INTERVAL_MS)
                    }
                }
                logger.logEvent("Price generation job started", TAG)
            }
        }
    }

    fun stopGenerating() {
        appScope.launch {
            generatingMutex.withLock {
                generatingJob?.cancel()
                generatingJob = null
            }
        }
    }

    private fun updatePrices() {
        currentStocks = currentStocks.map { stock ->
            val basePrice = stock.price
            val randomVariation = generatePriceVariation()
            val newPrice = if (randomVariation == BigDecimal.ZERO) {
                basePrice
            } else {
                basePrice.multiply(BigDecimal.ONE.add(randomVariation))
                    .setScale(2, RoundingMode.HALF_UP)
                    .coerceAtLeast(MIN_PRICE)
            }

            stock.copy(price = newPrice)
        }
    }

    private fun generatePriceVariation(): BigDecimal {
        val randomValue = Random.nextDouble(0.0, 1.0)
        return when {
            randomValue < PROBABILITY_NO_CHANGE -> BigDecimal.ZERO
            randomValue < PROBABILITY_NO_CHANGE + PROBABILITY_INCREASE -> {
                BigDecimal(Random.nextDouble(0.0, PRICE_VARIATION_MAX))
            }
            else -> {
                BigDecimal(Random.nextDouble(PRICE_VARIATION_MIN, 0.0))
            }
        }
    }

    companion object {
        private const val TAG = "${BuildConfig.APPLICATION_ID}.PriceGenerator"
        private const val PRICE_UPDATE_INTERVAL_MS = 2_000L
        private const val PRICE_VARIATION_MIN = -0.05
        private const val PRICE_VARIATION_MAX = 0.05
        private const val PROBABILITY_NO_CHANGE = 0.34
        private const val PROBABILITY_INCREASE = 0.33
        private val MIN_PRICE = BigDecimal("0.01")
    }

}
