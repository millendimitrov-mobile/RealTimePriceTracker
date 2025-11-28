package com.milen.realtimepricetracker.ui.feature.feed.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.feed_content_placeholder),
                style = MaterialTheme.typography.bodyLarge
            )
        }
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
                isFeedRunning = true
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
