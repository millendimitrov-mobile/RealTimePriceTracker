package com.milen.realtimepricetracker.ui.feature.feed.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.milen.realtimepricetracker.R
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
        when {
            state.error != null -> {
                ShowError(
                    errorMessage = state.error,
                    paddingValues = paddingValues,
                    onRetry = { onIntent(FeedIntent.Retry) },
                    onDismiss = { onIntent(FeedIntent.ClearError) }
                )
            }
            state.stocks.isEmpty() -> {
                ShowLoading(paddingValues)
            }
            else -> {
                ShowFeedsList(paddingValues, state, onIntent)
            }
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

@Composable
private fun ShowError(
    errorMessage: String,
    paddingValues: PaddingValues,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.close)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp)
        ) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Button(onClick = onRetry) {
                Text(stringResource(R.string.retry))
            }
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

@ThemePreviews
@Composable
private fun FeedContentPreviewError() {
    RealTimePriceTrackerTheme {
        FeedContent(
            state = FeedState(
                connectionStatus = ConnectionStatus.DISCONNECTED,
                isFeedRunning = false,
                error = "Connection failed. Please try again."
            ),
            onIntent = {}
        )
    }
}