package com.milen.realtimepricetracker.ui.feature.details.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.milen.realtimepricetracker.ui.feature.details.SymbolDetailsEvent
import com.milen.realtimepricetracker.ui.feature.details.SymbolDetailsViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
internal fun SymbolDetailsScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: SymbolDetailsViewModel = hiltViewModel<SymbolDetailsViewModel>(),
) {
    val state by viewModel.state.collectAsState()

    SymbolDetailsContent(
        modifier = modifier,
        state = state,
        onIntent = viewModel::handleIntent
    )

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is SymbolDetailsEvent.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }
}

