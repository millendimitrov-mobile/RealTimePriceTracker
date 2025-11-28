package com.milen.realtimepricetracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.milen.realtimepricetracker.ui.feature.feed.screen.FeedScreen

internal sealed class Screen(val route: String) {
    object Feed : Screen("feed")

    data class SymbolDetails(val symbol: String) : Screen(ROUTE) {
        companion object {
            private const val ROUTE_PREFIX = "symbol_details"
            const val ROUTE = "$ROUTE_PREFIX/{symbol}"
            fun createRoute(symbol: String) = "$ROUTE_PREFIX/$symbol"
        }
    }
}

@Composable
internal fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Feed.route,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.Feed.route) {
            FeedScreen()
        }

        composable(route = Screen.SymbolDetails.ROUTE) {
        }
    }
}

