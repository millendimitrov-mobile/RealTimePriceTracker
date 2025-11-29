package com.milen.realtimepricetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.milen.realtimepricetracker.R
import com.milen.realtimepricetracker.domain.model.PriceChangeDirection
import com.milen.realtimepricetracker.ui.annotations.ThemePreviews
import com.milen.realtimepricetracker.ui.theme.RealTimePriceTrackerTheme

@Composable
internal fun PriceChangeIndicator(
    direction: PriceChangeDirection,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    val (icon: ImageVector, tintColor, contentDescriptionRes: Int) = when (direction) {
        PriceChangeDirection.INCREASED -> {
            Triple(Icons.Default.ArrowUpward, colorScheme.secondary, R.string.price_increased)
        }

        PriceChangeDirection.DECREASED -> {
            Triple(Icons.Default.ArrowDownward, colorScheme.error, R.string.price_decreased)
        }

        PriceChangeDirection.NO_CHANGE,
        PriceChangeDirection.UNKNOWN -> {
            Triple(Icons.Default.Remove, colorScheme.onSurfaceVariant, R.string.price_unchanged)
        }
    }

    Box(
        modifier = modifier
            .size(32.dp)
            .background(
                color = when (direction) {
                    PriceChangeDirection.INCREASED -> colorScheme.secondary.copy(alpha = 0.2f)
                    PriceChangeDirection.DECREASED -> colorScheme.errorContainer
                    else -> colorScheme.surfaceVariant
                },
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = stringResource(contentDescriptionRes),
            tint = tintColor,
            modifier = Modifier.size(18.dp)
        )
    }
}

@ThemePreviews
@Composable
private fun PriceChangeIndicatorPreviewIncreased() {
    RealTimePriceTrackerTheme {
        PriceChangeIndicator(
            direction = PriceChangeDirection.INCREASED,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@ThemePreviews
@Composable
private fun PriceChangeIndicatorPreviewDecreased() {
    RealTimePriceTrackerTheme {
        PriceChangeIndicator(
            direction = PriceChangeDirection.DECREASED,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@ThemePreviews
@Composable
private fun PriceChangeIndicatorPreviewNoChange() {
    RealTimePriceTrackerTheme {
        PriceChangeIndicator(
            direction = PriceChangeDirection.NO_CHANGE,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@ThemePreviews
@Composable
private fun PriceChangeIndicatorPreviewUnknown() {
    RealTimePriceTrackerTheme {
        PriceChangeIndicator(
            direction = PriceChangeDirection.UNKNOWN,
            modifier = Modifier.padding(16.dp)
        )
    }
}