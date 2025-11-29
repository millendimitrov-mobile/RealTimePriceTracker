package com.milen.realtimepricetracker.ui.feature.feed

import android.content.Context
import app.cash.turbine.test
import com.milen.realtimepricetracker.data.mapper.SymbolMapper
import com.milen.realtimepricetracker.data.network.model.SymbolDto
import com.milen.realtimepricetracker.data.websocket.WebSocketRepository
import com.milen.realtimepricetracker.domain.logger.Logger
import com.milen.realtimepricetracker.domain.model.ConnectionStatus
import com.milen.realtimepricetracker.domain.model.StockSymbol
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {

    private lateinit var webSocketRepository: WebSocketRepository
    private lateinit var symbolMapper: SymbolMapper
    private lateinit var json: Json
    private lateinit var logger: Logger
    private lateinit var savedStateHandle: androidx.lifecycle.SavedStateHandle
    private lateinit var context: Context
    private lateinit var viewModel: FeedViewModel

    @Before
    fun setup() {
        webSocketRepository = mockk(relaxed = true)
        symbolMapper = mockk(relaxed = true)
        json = Json { ignoreUnknownKeys = true }
        logger = mockk(relaxed = true)
        savedStateHandle = mockk(relaxed = true)
        context = mockk(relaxed = true)

        every { savedStateHandle.getStateFlow<Boolean>(any(), any()) } returns MutableStateFlow(
            false
        )
        every { webSocketRepository.connectionStatus } returns MutableStateFlow(ConnectionStatus.DISCONNECTED)
        every { webSocketRepository.rawMessages } returns MutableSharedFlow(
            replay = 0,
            extraBufferCapacity = 100,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
        every { context.getString(com.milen.realtimepricetracker.R.string.error_connection_failed) } returns "Connection failed. Please try again."
        every { context.getString(com.milen.realtimepricetracker.R.string.error_parsing_failed) } returns "Failed to parse data. Please try again."

        viewModel = FeedViewModel(
            webSocketRepository = webSocketRepository,
            symbolMapper = symbolMapper,
            json = json,
            logger = logger,
            savedStateHandle = savedStateHandle,
            context = context
        )
    }

    @Test
    fun `initial state has correct default values`() = runTest {
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(ConnectionStatus.DISCONNECTED, state.connectionStatus)
            assertFalse(state.isFeedRunning)
            assertTrue(state.stocks.isEmpty())
            assertNull(state.error)
        }
    }

    @Test
    fun `handleIntent StartFeed sets savedStateHandle to true`() {
        viewModel.handleIntent(FeedIntent.StartFeed)

        verify { savedStateHandle["is_feed_running"] = true }
    }

    @Test
    fun `handleIntent StopFeed sets savedStateHandle to false`() {
        viewModel.handleIntent(FeedIntent.StopFeed)

        verify { savedStateHandle["is_feed_running"] = false }
    }

    @Test
    fun `handleIntent ToggleFeed toggles savedStateHandle value`() = runTest {
        val shouldFeedRunFlow = MutableStateFlow(false)
        every { savedStateHandle.getStateFlow<Boolean>(any(), any()) } returns shouldFeedRunFlow

        viewModel = FeedViewModel(
            webSocketRepository = webSocketRepository,
            symbolMapper = symbolMapper,
            json = json,
            logger = logger,
            savedStateHandle = savedStateHandle,
            context = context
        )

        viewModel.handleIntent(FeedIntent.ToggleFeed)

        verify { savedStateHandle["is_feed_running"] = true }
    }

    @Test
    fun `handleIntent SymbolClicked emits NavigateToSymbolDetails event`() = runTest {
        viewModel.events.test {
            viewModel.handleIntent(FeedIntent.SymbolClicked("AAPL"))

            val event = awaitItem()
            assertTrue(event is FeedEvent.NavigateToSymbolDetails)
            assertEquals("AAPL", (event as FeedEvent.NavigateToSymbolDetails).symbol)
        }
    }


    @Test
    fun `state updates when stocks are received`() = runTest(StandardTestDispatcher()) {
        val rawMessagesFlow = MutableSharedFlow<String>(
            replay = 1,
            extraBufferCapacity = 100,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
        every { webSocketRepository.rawMessages } returns rawMessagesFlow

        val dtos = listOf(
            SymbolDto("AAPL", "Apple", "Apple Inc.", BigDecimal("175.50")),
            SymbolDto("GOOGL", "Google", "Google LLC", BigDecimal("150.00"))
        )

        val stocks = listOf(
            StockSymbol("AAPL", "Apple", "Apple Inc.", BigDecimal("175.50")),
            StockSymbol("GOOGL", "Google", "Google LLC", BigDecimal("150.00"))
        )

        every { symbolMapper.toDomainList(any(), any()) } returns stocks

        viewModel = FeedViewModel(
            webSocketRepository = webSocketRepository,
            symbolMapper = symbolMapper,
            json = json,
            logger = logger,
            savedStateHandle = savedStateHandle,
            context = context
        )

        val jsonString = json.encodeToString(dtos)

        viewModel.state.test {
            val initialState = awaitItem()
            assertEquals(0, initialState.stocks.size)

            rawMessagesFlow.emit(jsonString)
            advanceUntilIdle()

            val updatedState = awaitItem()
            assertEquals(2, updatedState.stocks.size)
            assertEquals("AAPL", updatedState.stocks[0].id)
            assertEquals("GOOGL", updatedState.stocks[1].id)
        }
    }

    @Test
    fun `stocks are sorted by price descending`() = runTest(StandardTestDispatcher()) {
        val rawMessagesFlow = MutableSharedFlow<String>(
            replay = 1,
            extraBufferCapacity = 100,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
        every { webSocketRepository.rawMessages } returns rawMessagesFlow

        val dtos = listOf(
            SymbolDto("GOOGL", "Google", "Google LLC", BigDecimal("150.00")),
            SymbolDto("AAPL", "Apple", "Apple Inc.", BigDecimal("175.50"))
        )

        val stocks = listOf(
            StockSymbol("GOOGL", "Google", "Google LLC", BigDecimal("150.00")),
            StockSymbol("AAPL", "Apple", "Apple Inc.", BigDecimal("175.50"))
        )

        every { symbolMapper.toDomainList(any(), any()) } returns stocks

        viewModel = FeedViewModel(
            webSocketRepository = webSocketRepository,
            symbolMapper = symbolMapper,
            json = json,
            logger = logger,
            savedStateHandle = savedStateHandle,
            context = context
        )

        val jsonString = json.encodeToString(dtos)

        viewModel.state.test {
            val initialState = awaitItem()
            assertEquals(0, initialState.stocks.size)

            rawMessagesFlow.emit(jsonString)
            advanceUntilIdle()

            val updatedState = awaitItem()
            assertEquals(2, updatedState.stocks.size)
            assertEquals("AAPL", updatedState.stocks[0].id)
            assertEquals(BigDecimal("175.50"), updatedState.stocks[0].price)
            assertEquals("GOOGL", updatedState.stocks[1].id)
            assertEquals(BigDecimal("150.00"), updatedState.stocks[1].price)
        }
    }

    @Test
    fun `isFeedRunning is true when shouldRun is true and status is CONNECTED`() = runTest {
        val shouldFeedRunFlow = MutableStateFlow(true)
        val connectionStatusFlow = MutableStateFlow(ConnectionStatus.CONNECTED)
        every { savedStateHandle.getStateFlow<Boolean>(any(), any()) } returns shouldFeedRunFlow
        every { webSocketRepository.connectionStatus } returns connectionStatusFlow
        every { webSocketRepository.rawMessages } returns MutableSharedFlow<String>(
            replay = 0,
            extraBufferCapacity = 100,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

        viewModel = FeedViewModel(
            webSocketRepository = webSocketRepository,
            symbolMapper = symbolMapper,
            json = json,
            logger = logger,
            savedStateHandle = savedStateHandle,
            context = context
        )

        viewModel.state.test {
            skipItems(1)
            val state = awaitItem()
            assertTrue(state.isFeedRunning)
        }
    }

    @Test
    fun `isFeedRunning is false when shouldRun is false`() = runTest {
        val shouldFeedRunFlow = MutableStateFlow(false)
        val connectionStatusFlow = MutableStateFlow(ConnectionStatus.CONNECTED)
        every { savedStateHandle.getStateFlow<Boolean>(any(), any()) } returns shouldFeedRunFlow
        every { webSocketRepository.connectionStatus } returns connectionStatusFlow
        every { webSocketRepository.rawMessages } returns MutableSharedFlow<String>(
            replay = 0,
            extraBufferCapacity = 100,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

        viewModel = FeedViewModel(
            webSocketRepository = webSocketRepository,
            symbolMapper = symbolMapper,
            json = json,
            logger = logger,
            savedStateHandle = savedStateHandle,
            context = context
        )

        viewModel.state.test {
            skipItems(1)
            val state = awaitItem()
            assertFalse(state.isFeedRunning)
        }
    }

    @Test
    fun `handles invalid JSON gracefully and sets error`() = runTest(StandardTestDispatcher()) {
        val rawMessagesFlow = MutableSharedFlow<String>(
            replay = 1,
            extraBufferCapacity = 100,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
        every { webSocketRepository.rawMessages } returns rawMessagesFlow

        viewModel = FeedViewModel(
            webSocketRepository = webSocketRepository,
            symbolMapper = symbolMapper,
            json = json,
            logger = logger,
            savedStateHandle = savedStateHandle,
            context = context
        )

        viewModel.state.test {
            val initialState = awaitItem()
            assertEquals(0, initialState.stocks.size)
            assertNull(initialState.error)

            rawMessagesFlow.emit("invalid json")
            advanceUntilIdle()

            val errorState = awaitItem()
            assertEquals("Failed to parse data. Please try again.", errorState.error)
            verify(timeout = 2000) { logger.logError(any(), any(), any()) }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `sets error when connection fails from CONNECTING to DISCONNECTED`() =
        runTest(StandardTestDispatcher()) {
            val shouldFeedRunFlow = MutableStateFlow(true)
            val connectionStatusFlow =
                MutableStateFlow<ConnectionStatus>(ConnectionStatus.DISCONNECTED)
            every { savedStateHandle.getStateFlow<Boolean>(any(), any()) } returns shouldFeedRunFlow
            every { webSocketRepository.connectionStatus } returns connectionStatusFlow
            every { webSocketRepository.rawMessages } returns MutableSharedFlow<String>(
                replay = 0,
                extraBufferCapacity = 100,
                onBufferOverflow = BufferOverflow.DROP_OLDEST
            )

            viewModel = FeedViewModel(
                webSocketRepository = webSocketRepository,
                symbolMapper = symbolMapper,
                json = json,
                logger = logger,
                savedStateHandle = savedStateHandle,
                context = context
            )

            viewModel.state.test {
                skipItems(1)

                connectionStatusFlow.value = ConnectionStatus.CONNECTING
                advanceUntilIdle()
                skipItems(1)

                connectionStatusFlow.value = ConnectionStatus.DISCONNECTED
                advanceUntilIdle()

                var errorState = awaitItem()
                while (errorState.error == null) {
                    errorState = awaitItem()
                }
                assertEquals("Connection failed. Please try again.", errorState.error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `clears error when connection succeeds`() = runTest(StandardTestDispatcher()) {
        val connectionStatusFlow = MutableStateFlow<ConnectionStatus>(ConnectionStatus.DISCONNECTED)
        every { webSocketRepository.connectionStatus } returns connectionStatusFlow
        every { webSocketRepository.rawMessages } returns MutableSharedFlow<String>(
            replay = 0,
            extraBufferCapacity = 100,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )

        viewModel = FeedViewModel(
            webSocketRepository = webSocketRepository,
            symbolMapper = symbolMapper,
            json = json,
            logger = logger,
            savedStateHandle = savedStateHandle,
            context = context
        )

        viewModel.state.test {
            skipItems(1)

            connectionStatusFlow.value = ConnectionStatus.CONNECTED
            advanceUntilIdle()

            val connectedState = awaitItem()
            assertNull(connectedState.error)
        }
    }

    @Test
    fun `handleIntent Retry clears error and restarts connection`() =
        runTest(StandardTestDispatcher()) {
            val shouldFeedRunFlow = MutableStateFlow(true)
            val connectionStatusFlow = MutableStateFlow(ConnectionStatus.DISCONNECTED)
            every { savedStateHandle.getStateFlow<Boolean>(any(), any()) } returns shouldFeedRunFlow
            every { webSocketRepository.connectionStatus } returns connectionStatusFlow
            every { webSocketRepository.rawMessages } returns MutableSharedFlow(
                replay = 0,
                extraBufferCapacity = 100,
                onBufferOverflow = BufferOverflow.DROP_OLDEST
            )

            viewModel = FeedViewModel(
                webSocketRepository = webSocketRepository,
                symbolMapper = symbolMapper,
                json = json,
                logger = logger,
                savedStateHandle = savedStateHandle,
                context = context
            )

            viewModel.state.test {
                skipItems(1)

                connectionStatusFlow.value = ConnectionStatus.CONNECTING
                advanceUntilIdle()
                skipItems(1)

                connectionStatusFlow.value = ConnectionStatus.DISCONNECTED
                advanceUntilIdle()

                var errorState = awaitItem()
                while (errorState.error == null) {
                    errorState = awaitItem()
                }
                assertTrue(errorState.error != null)

                viewModel.handleIntent(FeedIntent.Retry)
                advanceUntilIdle()

                connectionStatusFlow.value = ConnectionStatus.CONNECTING
                advanceUntilIdle()

                var retryState = awaitItem()
                while (retryState.error != null) {
                    retryState = awaitItem()
                }
                assertNull(retryState.error)
                verify { webSocketRepository.start() }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleIntent ClearError clears error state`() = runTest(StandardTestDispatcher()) {
        val rawMessagesFlow = MutableSharedFlow<String>(
            replay = 1,
            extraBufferCapacity = 100,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
        every { webSocketRepository.rawMessages } returns rawMessagesFlow

        viewModel = FeedViewModel(
            webSocketRepository = webSocketRepository,
            symbolMapper = symbolMapper,
            json = json,
            logger = logger,
            savedStateHandle = savedStateHandle,
            context = context
        )

        viewModel.state.test {
            skipItems(1)

            rawMessagesFlow.emit("invalid json")
            advanceUntilIdle()

            val errorState = awaitItem()
            assertTrue(errorState.error != null)

            viewModel.handleIntent(FeedIntent.ClearError)
            advanceUntilIdle()

            val clearedState = awaitItem()
            assertNull(clearedState.error)
        }
    }
}

