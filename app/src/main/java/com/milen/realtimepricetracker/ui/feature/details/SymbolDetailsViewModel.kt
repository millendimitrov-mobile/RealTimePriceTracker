package com.milen.realtimepricetracker.ui.feature.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.milen.realtimepricetracker.BuildConfig
import com.milen.realtimepricetracker.data.mapper.SymbolMapper
import com.milen.realtimepricetracker.data.network.model.SymbolDto
import com.milen.realtimepricetracker.data.websocket.WebSocketRepository
import com.milen.realtimepricetracker.domain.logger.Logger
import com.milen.realtimepricetracker.domain.model.StockSymbol
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
internal class SymbolDetailsViewModel @Inject constructor(
    private val symbolMapper: SymbolMapper,
    private val json: Json,
    private val logger: Logger,
    webSocketRepository: WebSocketRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val symbolId: String = savedStateHandle.get<String>(KEY_SYMBOL)
        ?: throw IllegalArgumentException("Symbol ID is required")

    private val _events = Channel<SymbolDetailsEvent>(Channel.RENDEZVOUS)
    val events = _events.receiveAsFlow()

    private val currentSymbol = webSocketRepository.rawMessages
        .map { rawJson ->
            try {
                json.decodeFromString<List<SymbolDto>>(rawJson)
            } catch (e: Exception) {
                logger.logError("Failed to parse JSON message: ${e.message}", e, TAG)
                emptyList()
            }
        }
        .runningFold(emptyMap<String, StockSymbol>()) { previousStocksMap, newDtoStocks ->
            if (newDtoStocks.isEmpty()) {
                previousStocksMap
            } else {
                val mappedStocks = symbolMapper.toDomainList(previousStocksMap, newDtoStocks)
                mappedStocks.associateBy { it.id }
            }
        }
        .map { stocksMap ->
            stocksMap[symbolId]
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val state: StateFlow<SymbolDetailsState> = currentSymbol
        .map { stock ->
            SymbolDetailsState(
                symbol = stock,
                isLoading = stock == null,
                error = null
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SymbolDetailsState(isLoading = true)
        )

    fun handleIntent(intent: SymbolDetailsIntent) {
        when (intent) {
            is SymbolDetailsIntent.Back -> {
                _events.trySend(SymbolDetailsEvent.NavigateBack)
            }
        }
    }

    companion object {
        private const val TAG = "${BuildConfig.APPLICATION_ID}.SymbolDetailsViewModel"
        private const val KEY_SYMBOL = "symbol"
    }
}

