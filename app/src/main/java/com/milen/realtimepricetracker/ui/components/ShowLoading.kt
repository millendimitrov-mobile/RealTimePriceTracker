package com.milen.realtimepricetracker.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.milen.realtimepricetracker.ui.annotations.ThemePreviews

@Composable
internal fun ShowLoading(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}


@ThemePreviews
@Composable
private fun ShowLoadingPreview() {
    ShowLoading(
        paddingValues = PaddingValues()
    )
}