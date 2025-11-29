package com.milen.realtimepricetracker.data.websocket

import com.milen.realtimepricetracker.di.qualifiers.ApplicationScope
import com.milen.realtimepricetracker.domain.logger.Logger
import com.milen.realtimepricetracker.domain.model.ConnectionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class WebSocketRepository @Inject constructor(
    private val priceFeedService: PriceFeedService,
    private val logger: Logger,
    @param:ApplicationScope private val appScope: CoroutineScope,
) {

    private var messageSubscriptionJob: kotlinx.coroutines.Job? = null

    private val _connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus

    private val _rawMessages = MutableSharedFlow<String>(
        replay = 0,
        extraBufferCapacity = 100,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val rawMessages: SharedFlow<String> = _rawMessages

    init {
        observePriceFeedServiceConnection()
    }

    private fun observePriceFeedServiceConnection() {
        priceFeedService.connectionStatus
            .onEach { status ->
                _connectionStatus.update { status }
                when (status) {
                    ConnectionStatus.CONNECTED -> {
                        if (messageSubscriptionJob?.isActive != true) {
                            startMessageSubscription()
                        }
                    }
                    ConnectionStatus.DISCONNECTED -> {
                        stopMessageSubscription()
                    }
                    ConnectionStatus.CONNECTING -> {
                    }
                }
            }
            .launchIn(appScope)
    }

    private fun startMessageSubscription() {
        if (messageSubscriptionJob?.isActive == true) {
            logger.logEvent("Message subscription already active", TAG)
            return
        }

        messageSubscriptionJob = priceFeedService.receivedMessages
            .onEach { message ->
                _rawMessages.emit(message)
                logger.logEvent("Forwarded message to repository (${message.length} bytes)", TAG)
            }
            .launchIn(appScope)
        logger.logEvent("Started message subscription from PriceFeedService", TAG)
    }

    private fun stopMessageSubscription() {
        try {
            messageSubscriptionJob?.cancel()
            messageSubscriptionJob = null
            logger.logEvent("Stopped message subscription", TAG)
        } catch (e: Exception) {
            logger.logError("Error stopping message subscription: ${e.message}", e, TAG)
        }
    }

    fun start() {
        priceFeedService.start()
    }

    fun stop() {
        priceFeedService.stop()
    }

    companion object {
        private const val TAG = "WebSocketRepository"
    }
}


