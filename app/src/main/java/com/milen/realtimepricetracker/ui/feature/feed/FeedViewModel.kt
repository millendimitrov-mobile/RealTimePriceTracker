package com.milen.realtimepricetracker.ui.feature.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.milen.realtimepricetracker.domain.model.ConnectionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class FeedViewModel @Inject constructor() : ViewModel() {

    private val isFeedRunning = MutableStateFlow(false)
    private val connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)
    private val stocks = MutableStateFlow<List<Any>>(emptyList())
    private val isLoading = MutableStateFlow(false)

    val state: StateFlow<FeedState> = combine(
        stocks,
        connectionStatus,
        isFeedRunning,
        isLoading
    ) { stocksList, status, running, loading ->
        FeedState(
            stocks = stocksList,
            connectionStatus = status,
            isFeedRunning = running,
            isLoading = loading
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
        isFeedRunning.update { true }
        connectionStatus.update { ConnectionStatus.CONNECTING }
    }

    private fun stopFeed() {
        isFeedRunning.update { false }
        connectionStatus.update { ConnectionStatus.DISCONNECTED }
    }

    private fun toggleFeed() {
        if (isFeedRunning.value) {
            stopFeed()
        } else {
            startFeed()
        }
    }
}

