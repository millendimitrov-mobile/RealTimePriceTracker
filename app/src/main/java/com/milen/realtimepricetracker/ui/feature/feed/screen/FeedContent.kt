package com.milen.realtimepricetracker.ui.feature.feed.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.milen.realtimepricetracker.R
import com.milen.realtimepricetracker.domain.model.ConnectionStatus
import com.milen.realtimepricetracker.ui.annotations.ThemePreviews
import com.milen.realtimepricetracker.ui.components.AppScaffold
import com.milen.realtimepricetracker.ui.feature.feed.FeedIntent
import com.milen.realtimepricetracker.ui.feature.feed.FeedState
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
        if (state.rawMessages.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.feed_no_messages),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            // TODO REMOVE this is just for testing use LazyColumn with parsed Stocks instead
            RawMessageItem(
                message = state.rawMessages.firstOrNull().orEmpty(),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun RawMessageItem(
    message: String,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@ThemePreviews
@Composable
private fun FeedContentPreviewDisconnected() {
    RealTimePriceTrackerTheme {
        FeedContent(
            state = FeedState(
                connectionStatus = ConnectionStatus.DISCONNECTED,
                isFeedRunning = false
            ),
            onIntent = {}
        )
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
                rawMessages = listOf(
                    "Sample message 1",
                    "Sample message 2",
                    "Sample message 3"
                )
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
                isFeedRunning = true,
                isLoading = true
            ),
            onIntent = {}
        )
    }
}
