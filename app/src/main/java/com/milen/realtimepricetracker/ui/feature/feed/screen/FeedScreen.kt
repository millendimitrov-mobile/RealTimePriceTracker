package com.milen.realtimepricetracker.ui.feature.feed.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.milen.realtimepricetracker.ui.feature.feed.FeedViewModel

@Composable
internal fun FeedScreen(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()

    FeedContent(
        modifier = modifier,
        state = state,
        onIntent = viewModel::handleIntent
    )
}

