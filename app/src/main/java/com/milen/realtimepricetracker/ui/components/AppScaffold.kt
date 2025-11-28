package com.milen.realtimepricetracker.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection

@Composable
internal fun AppScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (paddingValues: PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        val layoutDirection = LocalLayoutDirection.current
        val safeDrawingPadding = WindowInsets.safeDrawing
            .only(WindowInsetsSides.Horizontal)
            .asPaddingValues()

        content(
            PaddingValues(
                start = innerPadding.calculateStartPadding(layoutDirection) +
                        safeDrawingPadding.calculateStartPadding(layoutDirection),
                top = innerPadding.calculateTopPadding(),
                end = innerPadding.calculateEndPadding(layoutDirection) +
                        safeDrawingPadding.calculateEndPadding(layoutDirection),
                bottom = innerPadding.calculateBottomPadding()
            )
        )
    }
}

