package com.milen.realtimepricetracker.ui.feature.feed.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.milen.realtimepricetracker.ui.feature.feed.FeedViewModel

@Composable
internal fun FeedScreen(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = hiltViewModel<FeedViewModel>(),
) {
    val state by viewModel.state.collectAsState()

    FeedContent(
        modifier = modifier,
        state = state,
        onIntent = viewModel::handleIntent
    )
}

