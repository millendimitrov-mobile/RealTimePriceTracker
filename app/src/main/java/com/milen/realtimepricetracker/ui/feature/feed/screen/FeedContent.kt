package com.milen.realtimepricetracker.ui.feature.feed.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.milen.realtimepricetracker.domain.model.ConnectionStatus
import com.milen.realtimepricetracker.ui.annotations.ThemePreviews
import com.milen.realtimepricetracker.ui.components.AppScaffold
import com.milen.realtimepricetracker.ui.components.ShowLoading
import com.milen.realtimepricetracker.ui.feature.feed.FeedIntent
import com.milen.realtimepricetracker.ui.feature.feed.FeedState
import com.milen.realtimepricetracker.ui.feature.feed.stockList
import com.milen.realtimepricetracker.ui.theme.RealTimePriceTrackerTheme

@Composable
internal fun FeedContent(
    state: FeedState,
    onIntent: (FeedIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScaffold(
        modifier = modifier,
        topBar = {
            FeedTopBar(
                connectionStatus = state.connectionStatus,
                isFeedRunning = state.isFeedRunning,
                onToggleFeed = { onIntent(FeedIntent.ToggleFeed) }
            )
        }
    ) { paddingValues ->
        if (state.stocks.isEmpty()) {
            ShowLoading(paddingValues)
        } else {
            ShowFeedsList(paddingValues, state, onIntent)
        }
    }
}


@Composable
private fun ShowFeedsList(
    paddingValues: PaddingValues,
    state: FeedState,
    onIntent: (FeedIntent) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = state.stocks,
            key = { stock -> stock.id }
        ) { stock ->
            StockRowItem(
                stock = stock,
                onClick = { onIntent(FeedIntent.SymbolClicked(stock.id)) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@ThemePreviews
@Composable
private fun FeedContentPreviewConnected() {
    RealTimePriceTrackerTheme {
        FeedContent(
            state = FeedState(
                connectionStatus = ConnectionStatus.CONNECTED,
                isFeedRunning = true,
                stocks = stockList
            ),
            onIntent = {}
        )
    }
}

@ThemePreviews
@Composable
private fun FeedContentPreviewConnecting() {
    RealTimePriceTrackerTheme {
        FeedContent(
            state = FeedState(
                connectionStatus = ConnectionStatus.CONNECTING,
                isFeedRunning = true
            ),
            onIntent = {}
        )
    }
}
