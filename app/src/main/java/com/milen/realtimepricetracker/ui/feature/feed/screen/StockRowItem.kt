package com.milen.realtimepricetracker.ui.feature.feed.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.milen.realtimepricetracker.R
import com.milen.realtimepricetracker.domain.model.PriceChangeDirection
import com.milen.realtimepricetracker.domain.model.StockSymbol
import com.milen.realtimepricetracker.ui.annotations.ThemePreviews
import com.milen.realtimepricetracker.ui.components.PriceChangeIndicator
import com.milen.realtimepricetracker.ui.feature.feed.stockList
import com.milen.realtimepricetracker.ui.theme.RealTimePriceTrackerTheme
import kotlinx.coroutines.delay
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

@Composable
internal fun StockRowItem(
    stock: StockSymbol,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    priceColoringOnChangeMillis: Long = 1000L,
) {
    LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val defaultPriceColor = colorScheme.onSurface
    var priceFlashColor by remember(stock.price) { mutableStateOf<Color?>(null) }
    val animatedPriceColor by animateColorAsState(
        targetValue = priceFlashColor ?: defaultPriceColor,
        animationSpec = tween(durationMillis = 300),
        label = "price_flash_animation"
    )

    LaunchedEffect(stock.price, stock.previousPrice) {
        when (stock.priceChangeDirection) {
            PriceChangeDirection.INCREASED -> {
                priceFlashColor = colorScheme.secondary
                delay(priceColoringOnChangeMillis)
                priceFlashColor = null
            }

            PriceChangeDirection.DECREASED -> {
                priceFlashColor = colorScheme.error
                delay(priceColoringOnChangeMillis)
                priceFlashColor = null
            }

            PriceChangeDirection.NO_CHANGE,
            PriceChangeDirection.UNKNOWN,
                -> {
                priceFlashColor = null
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = {
                onClick()
            }),
        colors = CardDefaults.cardColors(
            containerColor = colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        border = BorderStroke(
            width = 1.dp,
            color = colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stock.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stock.id,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = formatPrice(stock.price),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = animatedPriceColor
                    )
                }

                PriceChangeIndicator(
                    direction = stock.priceChangeDirection
                )

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = stringResource(R.string.navigate_to_details),
                    tint = colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}


private fun formatPrice(price: BigDecimal): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)
    return formatter.format(price)
}

@ThemePreviews
@Composable
private fun StockRowItemPreviewIncreased() {
    RealTimePriceTrackerTheme {
        StockRowItem(
            stock = stockList[0],
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@ThemePreviews
@Composable
private fun StockRowItemPreviewDecreased() {
    RealTimePriceTrackerTheme {
        StockRowItem(
            stock = stockList[1],
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@ThemePreviews
@Composable
private fun StockRowItemPreviewNoChange() {
    RealTimePriceTrackerTheme {
        StockRowItem(
            stock = stockList[2],
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
