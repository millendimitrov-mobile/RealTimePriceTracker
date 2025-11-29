package com.milen.realtimepricetracker.ui.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.milen.realtimepricetracker.BuildConfig
import com.milen.realtimepricetracker.data.mapper.SymbolMapper
import com.milen.realtimepricetracker.data.network.model.SymbolDto
import com.milen.realtimepricetracker.data.websocket.WebSocketRepository
import com.milen.realtimepricetracker.domain.logger.Logger
import com.milen.realtimepricetracker.domain.model.ConnectionStatus
import com.milen.realtimepricetracker.domain.model.StockSymbol
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
internal class FeedViewModel @Inject constructor(
    private val webSocketRepository: WebSocketRepository,
    private val symbolMapper: SymbolMapper,
    private val json: Json,
    private val logger: Logger,
) : ViewModel() {

    private val isLoading = MutableStateFlow(false)

    private val stocks = webSocketRepository.rawMessages
        .map { rawJson ->
            try {
                json.decodeFromString<List<SymbolDto>>(rawJson)
            } catch (e: Exception) {
                logger.logError("Failed to parse JSON message: ${e.message}", e, TAG)
                emptyList()
            }
        }
        .runningFold(emptyMap<String, StockSymbol>()) { previousStocks, newDtoStocks ->
            if (newDtoStocks.isEmpty()) {
                previousStocks
            } else {
                val mappedStocks = symbolMapper.toDomainList(previousStocks, newDtoStocks)
                mappedStocks.associateBy { it.id }
            }
        }
        .map { stocksMap ->
            stocksMap.values
                .sortedByDescending { it.price }
                .toList()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val state: StateFlow<FeedState> = combine(
        webSocketRepository.connectionStatus,
        isLoading,
        stocks
    ) { status, loading, stocksList ->
        FeedState(
            connectionStatus = status,
            isFeedRunning = status == ConnectionStatus.CONNECTED,
            isLoading = loading,
            stocks = stocksList
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FeedState(isLoading = true)
    )

    companion object {
        private const val TAG = "${BuildConfig.APPLICATION_ID}.FeedViewModel"
    }

    fun handleIntent(intent: FeedIntent) {
        when (intent) {
            is FeedIntent.StartFeed -> startFeed()
            is FeedIntent.StopFeed -> stopFeed()
            is FeedIntent.ToggleFeed -> toggleFeed()
            is FeedIntent.SymbolClicked -> Unit
        }
    }

    private fun startFeed() {
        webSocketRepository.start()
    }

    private fun stopFeed() {
        webSocketRepository.stop()
    }

    private fun toggleFeed() {
        val currentStatus = webSocketRepository.connectionStatus.value
        if (currentStatus == ConnectionStatus.CONNECTED) {
            stopFeed()
        } else {
            startFeed()
        }
    }
}
