package com.milen.realtimepricetracker.ui.feature.feed

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.milen.realtimepricetracker.BuildConfig
import com.milen.realtimepricetracker.R
import com.milen.realtimepricetracker.data.mapper.SymbolMapper
import com.milen.realtimepricetracker.data.network.model.SymbolDto
import com.milen.realtimepricetracker.data.websocket.WebSocketRepository
import com.milen.realtimepricetracker.domain.logger.Logger
import com.milen.realtimepricetracker.domain.model.ConnectionStatus
import com.milen.realtimepricetracker.domain.model.StockSymbol
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
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
    private val savedStateHandle: SavedStateHandle,
    @param:ApplicationContext private val context: Context,
) : ViewModel() {

    private val shouldFeedRun = savedStateHandle.getStateFlow(KEY_IS_FEED_RUNNING, false)

    private val autoSyncFeedJob: Job = combine(
        shouldFeedRun,
        webSocketRepository.connectionStatus
    ) { shouldRun, status ->
        Pair(shouldRun, status)
    }.onEach { (shouldRun, status) ->
        when {
            shouldRun && status != ConnectionStatus.CONNECTED -> {
                error.value = null
                webSocketRepository.start()
            }

            !shouldRun && status == ConnectionStatus.CONNECTED -> {
                webSocketRepository.stop()
            }
        }
    }.launchIn(viewModelScope)

    private var previousConnectionStatus: ConnectionStatus? = null

    private val connectionErrorMonitoringJob: Job = webSocketRepository.connectionStatus
        .onEach { status ->
            val previous = previousConnectionStatus
            previousConnectionStatus = status

            when {
                status == ConnectionStatus.CONNECTED -> {
                    error.value = null
                }
                previous == ConnectionStatus.CONNECTING && status == ConnectionStatus.DISCONNECTED && shouldFeedRun.value -> {
                    error.value = context.getString(R.string.error_connection_failed)
                }
            }
        }
        .launchIn(viewModelScope)

    private val _events = Channel<FeedEvent>(Channel.RENDEZVOUS)
    val events = _events.receiveAsFlow()

    private val isLoading = MutableStateFlow(false)
    private val error = MutableStateFlow<String?>(null)

    private val stocks = webSocketRepository.rawMessages
        .map { rawJson ->
            try {
                json.decodeFromString<List<SymbolDto>>(rawJson)
            } catch (e: Exception) {
                logger.logError("Failed to parse JSON message: ${e.message}", e, TAG)
                error.value = context.getString(R.string.error_parsing_failed)
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
        stocks,
        shouldFeedRun,
        error
    ) { status, loading, stocksList, shouldRun, errorMessage ->
        FeedState(
            connectionStatus = status,
            isFeedRunning = shouldRun && status == ConnectionStatus.CONNECTED,
            stocks = stocksList,
            error = errorMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FeedState()
    )

    fun handleIntent(intent: FeedIntent) {
        when (intent) {
            is FeedIntent.StartFeed -> startFeed()
            is FeedIntent.StopFeed -> stopFeed()
            is FeedIntent.ToggleFeed -> toggleFeed()
            is FeedIntent.SymbolClicked -> {
                _events.trySend(FeedEvent.NavigateToSymbolDetails(intent.symbol))
            }
            is FeedIntent.Retry -> {
                error.value = null
                if (shouldFeedRun.value) {
                    webSocketRepository.start()
                }
            }
            is FeedIntent.ClearError -> {
                error.value = null
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        autoSyncFeedJob.cancel()
        connectionErrorMonitoringJob.cancel()
    }

    private fun startFeed() {
        savedStateHandle[KEY_IS_FEED_RUNNING] = true
    }

    private fun stopFeed() {
        savedStateHandle[KEY_IS_FEED_RUNNING] = false
    }

    private fun toggleFeed() {
        val currentShouldRun = shouldFeedRun.value
        savedStateHandle[KEY_IS_FEED_RUNNING] = !currentShouldRun
    }

    companion object {
        private const val TAG = "${BuildConfig.APPLICATION_ID}.FeedViewModel"
        private const val KEY_IS_FEED_RUNNING = "is_feed_running"
    }
}
