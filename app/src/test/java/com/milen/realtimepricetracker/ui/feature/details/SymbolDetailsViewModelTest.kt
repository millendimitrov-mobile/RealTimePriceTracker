package com.milen.realtimepricetracker.ui.feature.details

import app.cash.turbine.test
import com.milen.realtimepricetracker.data.mapper.SymbolMapper
import com.milen.realtimepricetracker.data.network.model.SymbolDto
import com.milen.realtimepricetracker.data.websocket.WebSocketRepository
import com.milen.realtimepricetracker.domain.logger.Logger
import com.milen.realtimepricetracker.domain.model.StockSymbol
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class SymbolDetailsViewModelTest {

    private lateinit var webSocketRepository: WebSocketRepository
    private lateinit var symbolMapper: SymbolMapper
    private lateinit var json: Json
    private lateinit var logger: Logger
    private lateinit var savedStateHandle: androidx.lifecycle.SavedStateHandle
    private lateinit var viewModel: SymbolDetailsViewModel

    @Before
    fun setup() {
        webSocketRepository = mockk(relaxed = true)
        symbolMapper = mockk(relaxed = true)
        json = Json { ignoreUnknownKeys = true }
        logger = mockk(relaxed = true)
        savedStateHandle = mockk(relaxed = true)

        every { savedStateHandle.get<String>("symbol") } returns "AAPL"
        every { webSocketRepository.rawMessages } returns MutableSharedFlow<String>(
            replay = 0,
            extraBufferCapacity = 100,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }

    @Test
    fun `initial state has isLoading true when symbol is null`() = runTest {
        viewModel = SymbolDetailsViewModel(
            webSocketRepository = webSocketRepository,
            symbolMapper = symbolMapper,
            json = json,
            logger = logger,
            savedStateHandle = savedStateHandle
        )

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.isLoading)
            assertNull(state.symbol)
            assertNull(state.error)
        }
    }


    @Test
    fun `state updates when symbol is received`() = runTest(StandardTestDispatcher()) {
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

        viewModel = SymbolDetailsViewModel(
            webSocketRepository = webSocketRepository,
            symbolMapper = symbolMapper,
            json = json,
            logger = logger,
            savedStateHandle = savedStateHandle
        )

        val jsonString = json.encodeToString(dtos)

        viewModel.state.test {
            val initialState = awaitItem()
            assertTrue(initialState.isLoading)
            assertNull(initialState.symbol)

            rawMessagesFlow.emit(jsonString)
            advanceUntilIdle()

            val updatedState = awaitItem()
            assertTrue(updatedState.symbol != null)
            assertEquals("AAPL", updatedState.symbol?.id)
            assertEquals("Apple", updatedState.symbol?.name)
            assertEquals(BigDecimal("175.50"), updatedState.symbol?.price)
            assertTrue(!updatedState.isLoading)
        }
    }

    @Test
    fun `state filters for correct symbol ID`() = runTest(StandardTestDispatcher()) {
        val rawMessagesFlow = MutableSharedFlow<String>(
            replay = 1,
            extraBufferCapacity = 100,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
        every { webSocketRepository.rawMessages } returns rawMessagesFlow
        every { savedStateHandle.get<String>("symbol") } returns "GOOGL"

        val dtos = listOf(
            SymbolDto("AAPL", "Apple", "Apple Inc.", BigDecimal("175.50")),
            SymbolDto("GOOGL", "Google", "Google LLC", BigDecimal("150.00"))
        )

        val stocks = listOf(
            StockSymbol("AAPL", "Apple", "Apple Inc.", BigDecimal("175.50")),
            StockSymbol("GOOGL", "Google", "Google LLC", BigDecimal("150.00"))
        )

        every { symbolMapper.toDomainList(any(), any()) } returns stocks

        viewModel = SymbolDetailsViewModel(
            webSocketRepository = webSocketRepository,
            symbolMapper = symbolMapper,
            json = json,
            logger = logger,
            savedStateHandle = savedStateHandle
        )

        val jsonString = json.encodeToString(dtos)

        viewModel.state.test {
            val initialState = awaitItem()
            assertTrue(initialState.isLoading)

            rawMessagesFlow.emit(jsonString)
            advanceUntilIdle()

            val updatedState = awaitItem()
            assertTrue(updatedState.symbol != null)
            assertEquals("GOOGL", updatedState.symbol?.id)
            assertEquals("Google", updatedState.symbol?.name)
        }
    }

    @Test
    fun `state remains null when symbol not found in updates`() =
        runTest(StandardTestDispatcher()) {
            val rawMessagesFlow = MutableSharedFlow<String>(
                replay = 1,
                extraBufferCapacity = 100,
                onBufferOverflow = BufferOverflow.DROP_OLDEST
            )
            every { webSocketRepository.rawMessages } returns rawMessagesFlow
            every { savedStateHandle.get<String>("symbol") } returns "MSFT"

            val dtos = listOf(
                SymbolDto("AAPL", "Apple", "Apple Inc.", BigDecimal("175.50")),
                SymbolDto("GOOGL", "Google", "Google LLC", BigDecimal("150.00"))
            )

            val stocks = listOf(
                StockSymbol("AAPL", "Apple", "Apple Inc.", BigDecimal("175.50")),
                StockSymbol("GOOGL", "Google", "Google LLC", BigDecimal("150.00"))
            )

            every { symbolMapper.toDomainList(any(), any()) } returns stocks

            viewModel = SymbolDetailsViewModel(
                webSocketRepository = webSocketRepository,
                symbolMapper = symbolMapper,
                json = json,
                logger = logger,
                savedStateHandle = savedStateHandle
            )

            val jsonString = json.encodeToString(dtos)

            viewModel.state.test {
                val initialState = awaitItem()
                assertTrue(initialState.isLoading)
                assertNull(initialState.symbol)

                rawMessagesFlow.emit(jsonString)
                advanceUntilIdle()

                verify(timeout = 1000) { symbolMapper.toDomainList(any(), any()) }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleIntent Back emits NavigateBack event`() = runTest {
        viewModel = SymbolDetailsViewModel(
            webSocketRepository = webSocketRepository,
            symbolMapper = symbolMapper,
            json = json,
            logger = logger,
            savedStateHandle = savedStateHandle
        )

        viewModel.events.test {
            viewModel.handleIntent(SymbolDetailsIntent.Back)

            val event = awaitItem()
            assertTrue(event is SymbolDetailsEvent.NavigateBack)
        }
    }

    @Test
    fun `handles invalid JSON gracefully`() = runTest(StandardTestDispatcher()) {
        val rawMessagesFlow = MutableSharedFlow<String>(
            replay = 1,
            extraBufferCapacity = 100,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
        every { webSocketRepository.rawMessages } returns rawMessagesFlow

        viewModel = SymbolDetailsViewModel(
            webSocketRepository = webSocketRepository,
            symbolMapper = symbolMapper,
            json = json,
            logger = logger,
            savedStateHandle = savedStateHandle
        )

        viewModel.state.test {
            val initialState = awaitItem()
            assertTrue(initialState.isLoading)

            rawMessagesFlow.emit("invalid json")
            advanceUntilIdle()

            verify(timeout = 2000) { logger.logError(any(), any(), any()) }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws exception when symbol ID is missing from savedStateHandle`() {
        every { savedStateHandle.get<String>("symbol") } returns null

        SymbolDetailsViewModel(
            webSocketRepository = webSocketRepository,
            symbolMapper = symbolMapper,
            json = json,
            logger = logger,
            savedStateHandle = savedStateHandle
        )
    }
}

