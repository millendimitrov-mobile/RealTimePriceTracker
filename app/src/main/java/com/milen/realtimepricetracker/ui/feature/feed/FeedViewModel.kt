package com.milen.realtimepricetracker.ui.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.milen.realtimepricetracker.data.websocket.WebSocketRepository
import com.milen.realtimepricetracker.domain.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class FeedViewModel @Inject constructor(
    private val webSocketRepository: WebSocketRepository,
    private val logger: Logger,
) : ViewModel() {

    private val isFeedRunning = MutableStateFlow(false)
    private val isLoading = MutableStateFlow(false)

    private val rawMessages = webSocketRepository.rawMessages
        .runningFold(emptyList<String>()) { acc, message ->
            (acc + message).takeLast(50)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val state: StateFlow<FeedState> = combine(
        webSocketRepository.connectionStatus,
        isFeedRunning,
        isLoading,
        rawMessages
    ) { status, running, loading, messages ->
        FeedState(
            connectionStatus = status,
            isFeedRunning = running,
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
            is FeedIntent.SymbolClicked -> Unit // TODO Handle symbol click
        }
    }

    private fun startFeed() {
        isFeedRunning.update { true }
        viewModelScope.launch {
            try {
                webSocketRepository.connect()
            } catch (e: Exception) {
                logger.logError("Failed to start feed: ${e.message}", e)
                isFeedRunning.update { false }
            }
        }
    }

    private fun stopFeed() {
        isFeedRunning.update { false }
        viewModelScope.launch {
            webSocketRepository.disconnect()
        }
    }

    private fun toggleFeed() {
        if (isFeedRunning.value) {
            stopFeed()
        } else {
            startFeed()
        }
    }
}

