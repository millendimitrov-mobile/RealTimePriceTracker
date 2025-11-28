package com.milen.realtimepricetracker.ui.feature.feed.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.milen.realtimepricetracker.R
import com.milen.realtimepricetracker.domain.model.ConnectionStatus
import com.milen.realtimepricetracker.ui.annotations.ThemePreviews
import com.milen.realtimepricetracker.ui.theme.RealTimePriceTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FeedTopBar(
    connectionStatus: ConnectionStatus,
    isFeedRunning: Boolean,
    onToggleFeed: () -> Unit,
) {
    TopAppBar(
        title = { Text(stringResource(R.string.feed_title)) },
        navigationIcon = {
            Text(
                text = when (connectionStatus) {
                    ConnectionStatus.CONNECTED -> "🟢"
                    ConnectionStatus.DISCONNECTED -> "🔴"
                    ConnectionStatus.CONNECTING -> "🟡"
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        },
        actions = {
            TextButton(onClick = onToggleFeed) {
                Text(
                    text = stringResource(
                        if (isFeedRunning) R.string.feed_stop else R.string.feed_start
                    )
                )
            }
        }
    )
}

@ThemePreviews
@Composable
private fun FeedTopBarPreviewDisconnected() {
    RealTimePriceTrackerTheme {
        FeedTopBar(
            connectionStatus = ConnectionStatus.DISCONNECTED,
            isFeedRunning = false,
            onToggleFeed = {}
        )
    }
}

@ThemePreviews
@Composable
private fun FeedTopBarPreviewConnecting() {
    RealTimePriceTrackerTheme {
        FeedTopBar(
            connectionStatus = ConnectionStatus.CONNECTING,
            isFeedRunning = true,
            onToggleFeed = {}
        )
    }
}

@ThemePreviews
@Composable
private fun FeedTopBarPreviewConnected() {
    RealTimePriceTrackerTheme {
        FeedTopBar(
            connectionStatus = ConnectionStatus.CONNECTED,
            isFeedRunning = true,
            onToggleFeed = {}
        )
    }
}