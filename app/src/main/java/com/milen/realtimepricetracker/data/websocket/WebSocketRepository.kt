package com.milen.realtimepricetracker.data.websocket

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
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class WebSocketRepository @Inject constructor(
    private val httpClient: HttpClient,
    private val logger: Logger,
    @param:ApplicationScope private val appScope: CoroutineScope,
) {

    private val connectMutex = Mutex()
    private var webSocketSession: WebSocketSession? = null

    private val _connectionStatus = MutableStateFlow(ConnectionStatus.DISCONNECTED)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus

    private val _rawMessages = MutableSharedFlow<String>(
        replay = 0,
        extraBufferCapacity = 100,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val rawMessages: SharedFlow<String> = _rawMessages

    suspend fun connect() {
        connectMutex.withLock {
            if (webSocketSession != null) {
                logger.logEvent("WebSocket already connected, skipping connection attempt", TAG)
                return
            }

            require(WebSocketConfig.RAW_URL.startsWith("wss://")) {
                "WebSocket URL must use secure connection (wss://), got: ${WebSocketConfig.RAW_URL}"
            }

            try {
                _connectionStatus.update { ConnectionStatus.CONNECTING }
                logger.logEvent("Connecting to secure WebSocket: ${WebSocketConfig.RAW_URL}", TAG)

                webSocketSession = httpClient.webSocketSession(WebSocketConfig.RAW_URL)

                _connectionStatus.update { ConnectionStatus.CONNECTED }
                logger.logEvent("WebSocket connected successfully", TAG)

                appScope.launch {
                    receiveMessages()
                }
            } catch (e: Exception) {
                logger.logError("Failed to connect WebSocket: ${e.message}", e, TAG)
                _connectionStatus.update { ConnectionStatus.DISCONNECTED }
                webSocketSession = null
                throw e
            }
        }
    }

    suspend fun disconnect() {
        connectMutex.withLock {
            val session = webSocketSession
            if (session != null) {
                try {
                    // Close connection gracefully with normal closure reason
                    session.close(CloseReason(CloseReason.Codes.NORMAL, "Client disconnecting"))
                    logger.logEvent("WebSocket disconnected gracefully", TAG)
                } catch (e: Exception) {
                    logger.logError("Error during WebSocket disconnect: ${e.message}", e, TAG)
                } finally {
                    webSocketSession = null
                    _connectionStatus.update { ConnectionStatus.DISCONNECTED }
                }
            } else {
                logger.logEvent("disconnect() called but WebSocket was already null", TAG)
            }
        }
    }

    private suspend fun receiveMessages() {
        val session = webSocketSession ?: return

        try {
            for (frame in session.incoming) {
                when (frame) {
                    is Frame.Text -> {
                        handleTextFrame(frame)
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

    private suspend fun handleTextFrame(frame: Frame.Text) {
        try {
            val text = frame.readText()
            if (text.length <= MAX_MESSAGE_SIZE) {
                _rawMessages.emit(text)
            } else {
                logger.logError(
                    "Received message too large (${text.length} bytes), ignoring",
                    null,
                    TAG
                )
            }
        } catch (e: Exception) {
            logger.logError("Error reading frame text: ${e.message}", e, TAG)
        }
    }

    suspend fun sendMessage(message: String) {
        val session = webSocketSession
        if (session == null) {
            logger.logError("Cannot send message: WebSocket not connected", null, TAG)
            return
        }

        try {
            if (message.length > MAX_MESSAGE_SIZE) {
                logger.logError("Message too large to send (${message.length} bytes)", null, TAG)
                throw IllegalArgumentException("Message size exceeds maximum allowed size")
            }

            session.send(Frame.Text(message))
            logger.logEvent("Message sent successfully (${message.length} bytes)", TAG)
        } catch (e: Exception) {
            logger.logError("Error sending message: ${e.message}", e, TAG)
            _connectionStatus.update { ConnectionStatus.DISCONNECTED }
            webSocketSession = null
            throw e
        }
    }

    companion object {
        private const val TAG = "WebSocketRepository"
        private const val MAX_MESSAGE_SIZE = 1_000_000
    }
}


