package com.milen.realtimepricetracker.ui.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.milen.realtimepricetracker.data.websocket.WebSocketRepository
import com.milen.realtimepricetracker.domain.logger.Logger
import com.milen.realtimepricetracker.domain.model.ConnectionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class FeedViewModel @Inject constructor(
    private val webSocketRepository: WebSocketRepository,
    private val logger: Logger,
) : ViewModel() {

    private val isLoading = MutableStateFlow(false)

    private val rawMessages = webSocketRepository.rawMessages
        .runningFold(emptyList<String>()) { acc, message ->
            logger.logEvent("acc: $acc")
            logger.logEvent("message: $message")
            listOf(message) // TODO MAP IT TO STOCKS
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val state: StateFlow<FeedState> = combine(
        webSocketRepository.connectionStatus,
        isLoading,
        rawMessages
    ) { status, loading, messages ->
        FeedState(
            connectionStatus = status,
            isFeedRunning = status == ConnectionStatus.CONNECTED,
            isLoading = loading,
            rawMessages = messages
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FeedState(isLoading = true)
    )

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
