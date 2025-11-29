package com.milen.realtimepricetracker.data.websocket

import com.milen.realtimepricetracker.BuildConfig
import com.milen.realtimepricetracker.data.config.WebSocketConfig
import com.milen.realtimepricetracker.di.qualifiers.ApplicationScope
import com.milen.realtimepricetracker.domain.logger.Logger
import com.milen.realtimepricetracker.domain.model.ConnectionStatus
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class PriceFeedService @Inject constructor(
    private val priceGenerator: PriceGenerator,
    private val httpClient: HttpClient,
    @param:ApplicationScope private val appScope: CoroutineScope,
    private val logger: Logger,
) {

    private val connectionMutex = Mutex()
    private var webSocketSession: WebSocketSession? = null
    private var sendingJob: Job? = null
    private var receivingJob: Job? = null

    private val _connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus

    private val _receivedMessages = MutableSharedFlow<String>(
        replay = 0,
        extraBufferCapacity = 100,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val receivedMessages: SharedFlow<String> = _receivedMessages.asSharedFlow()

    fun start() {
        appScope.launch {
            connectionMutex.withLock {
                if (webSocketSession != null) {
                    logger.logEvent("PriceFeedService already started", TAG)
                    return@withLock
                }

                try {
                    require(WebSocketConfig.RAW_URL.startsWith("wss://")) {
                        "WebSocket URL must use secure connection (wss://), got: ${WebSocketConfig.RAW_URL}"
                    }

                    _connectionStatus.update { ConnectionStatus.CONNECTING }
                    logger.logEvent(
                        "Connecting PriceFeedService to: ${WebSocketConfig.RAW_URL}",
                        TAG
                    )
                    webSocketSession = httpClient.webSocketSession(WebSocketConfig.RAW_URL)
                    _connectionStatus.update { ConnectionStatus.CONNECTED }
                    logger.logEvent("PriceFeedService connected successfully", TAG)

                    logger.logEvent("Starting price generator", TAG)
                    priceGenerator.startGenerating()

                    sendingJob = appScope.launch {
                        logger.logEvent("Starting to collect price updates", TAG)
                        priceGenerator.priceUpdates.collect { jsonString ->
                            try {
                                logger.logEvent(
                                    "Received price update, length: ${jsonString.length}",
                                    TAG
                                )
                                val session = webSocketSession
                                if (session != null) {
                                    try {
                                        session.send(Frame.Text(jsonString))
                                        logger.logEvent(
                                            "Price update sent successfully (${jsonString.length} bytes)",
                                            TAG
                                        )
                                    } catch (e: Exception) {
                                        logger.logError(
                                            "Cannot send message: WebSocket session error: ${e.message}",
                                            e,
                                            TAG
                                        )
                                    }
                                } else {
                                    logger.logError(
                                        "Cannot send message: WebSocket not connected",
                                        null,
                                        TAG
                                    )
                                }
                            } catch (e: Exception) {
                                logger.logError("Error sending price updates: ${e.message}", e, TAG)
                            }
                        }
                    }
                    logger.logEvent("Price update collection job started", TAG)

                    receivingJob = appScope.launch {
                        logger.logEvent("Starting to receive WebSocket messages", TAG)
                        receiveMessages()
                    }
                    logger.logEvent("WebSocket message receiving job started", TAG)
                } catch (e: Exception) {
                    logger.logError("Failed to start PriceFeedService: ${e.message}", e, TAG)
                    _connectionStatus.update { ConnectionStatus.DISCONNECTED }
                    webSocketSession = null
                    priceGenerator.stopGenerating()
                }
            }
        }
    }

    fun stop() {
        appScope.launch {
            connectionMutex.withLock {
                sendingJob?.cancel()
                sendingJob = null
                receivingJob?.cancel()
                receivingJob = null
                priceGenerator.stopGenerating()

                val session = webSocketSession
                if (session != null) {
                    try {
                        session.close(
                            CloseReason(
                                CloseReason.Codes.NORMAL,
                                "PriceFeedService stopping"
                            )
                        )
                        logger.logEvent("PriceFeedService disconnected", TAG)
                    } catch (e: Exception) {
                        logger.logError("Error closing WebSocket: ${e.message}", e, TAG)
                    } finally {
                        webSocketSession = null
                        _connectionStatus.update { ConnectionStatus.DISCONNECTED }
                    }
                }
            }
        }
    }

    private suspend fun receiveMessages() {
        val session = webSocketSession ?: return

        try {
            for (frame in session.incoming) {
                when (frame) {
                    is Frame.Text -> {
                        try {
                            val text = frame.readText()
                            logger.logEvent(
                                "Received WebSocket message, length: ${text.length}",
                                TAG
                            )
                            _receivedMessages.emit(text)
                        } catch (e: Exception) {
                            logger.logError("Error reading frame text: ${e.message}", e, TAG)
                        }
                    }

                    is Frame.Close -> {
                        logger.logEvent("WebSocket closed by server", TAG)
                        break
                    }

                    is Frame.Ping -> {
                        logger.logEvent("Received ping frame", TAG)
                    }

                    is Frame.Pong -> {
                        logger.logEvent("Received pong frame", TAG)
                    }

                    else -> {
                        logger.logEvent(
                            "Received unsupported frame type: ${frame::class.simpleName}",
                            TAG
                        )
                    }
                }
            }
        } catch (e: Exception) {
            logger.logError("Error receiving WebSocket messages: ${e.message}", e, TAG)
        } finally {
            webSocketSession = null
            _connectionStatus.update { ConnectionStatus.DISCONNECTED }
        }
    }

    companion object {
        private const val TAG = "${BuildConfig.APPLICATION_ID}.PriceFeedService"
    }
}

