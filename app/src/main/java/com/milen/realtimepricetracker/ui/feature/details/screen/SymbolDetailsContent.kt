package com.milen.realtimepricetracker.ui.feature.details.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.milen.realtimepricetracker.R
import com.milen.realtimepricetracker.ui.annotations.ThemePreviews
import com.milen.realtimepricetracker.ui.components.AppScaffold
import com.milen.realtimepricetracker.ui.components.PriceChangeIndicator
import com.milen.realtimepricetracker.ui.components.ShowLoading
import com.milen.realtimepricetracker.ui.feature.details.SymbolDetailsIntent
import com.milen.realtimepricetracker.ui.feature.details.SymbolDetailsState
import com.milen.realtimepricetracker.ui.theme.RealTimePriceTrackerTheme
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

@Composable
internal fun SymbolDetailsContent(
    state: SymbolDetailsState,
    onIntent: (SymbolDetailsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    AppScaffold(
        modifier = modifier,
        topBar = {
            SymbolDetailsTopBar(
                onBackClick = { onIntent(SymbolDetailsIntent.Back) }
            )
        }
    ) { paddingValues ->
        when {
            state.isLoading -> {
                ShowLoading(paddingValues)
            }

            state.symbol != null -> {
                SymbolDetailsBody(
                    symbol = state.symbol,
                    paddingValues = paddingValues
                )
            }

            state.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.error,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SymbolDetailsTopBar(
    onBackClick: () -> Unit,
) {
    TopAppBar(
        title = { Text(stringResource(R.string.symbol_details_title)) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        }
    )
}

@Composable
private fun SymbolDetailsBody(
    symbol: com.milen.realtimepricetracker.domain.model.StockSymbol,
    paddingValues: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = symbol.name,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = symbol.id,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = formatPrice(symbol.price),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            PriceChangeIndicator(
                direction = symbol.priceChangeDirection
            )

            if (symbol.previousPrice != null) {
                val priceChange = symbol.price - symbol.previousPrice
                val priceChangePercent = (priceChange / symbol.previousPrice) * BigDecimal("100")
                val sign = if (priceChange >= BigDecimal.ZERO) "+" else ""

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "$sign${formatPrice(priceChange)}",
                        style = MaterialTheme.typography.titleLarge,
                        color = when {
                            priceChange > BigDecimal.ZERO -> MaterialTheme.colorScheme.secondary
                            priceChange < BigDecimal.ZERO -> MaterialTheme.colorScheme.error
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )
                    Text(
                        text = "$sign${String.format("%.2f", priceChangePercent)}%",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Text(
            text = symbol.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

private fun formatPrice(price: BigDecimal): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)
    return formatter.format(price)
}

@ThemePreviews
@Composable
private fun SymbolDetailsContentPreview() {
    RealTimePriceTrackerTheme {
        SymbolDetailsContent(
            state = SymbolDetailsState(
                symbol = com.milen.realtimepricetracker.domain.model.StockSymbol(
                    id = "AAPL",
                    name = "Apple",
                    description = "Apple Inc. designs, manufactures, and markets smartphones, personal computers, tablets, wearables, and accessories worldwide.",
                    price = BigDecimal("175.50"),
                    previousPrice = BigDecimal("170.00")
                ),
                isLoading = false
            ),
            onIntent = {}
        )
    }
}

