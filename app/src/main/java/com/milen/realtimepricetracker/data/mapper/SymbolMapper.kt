package com.milen.realtimepricetracker.data.mapper

import com.milen.realtimepricetracker.data.network.model.SymbolDto
import com.milen.realtimepricetracker.domain.model.StockSymbol
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SymbolMapper @Inject constructor() {

    fun toDomain(
        dto: SymbolDto,
        previousPrice: java.math.BigDecimal? = null,
    ): StockSymbol {
        return StockSymbol(
            id = dto.id,
            name = dto.name,
            description = dto.description,
            price = dto.price,
            previousPrice = previousPrice,
            lastUpdate = System.currentTimeMillis()
        )
    }

    fun toDomainList(
        previousStocks: Map<String, StockSymbol> = emptyMap(),
        dtos: List<SymbolDto>,
    ): List<StockSymbol> {
        return dtos.map { dto ->
            val previous = previousStocks[dto.id]
            toDomain(dto, previous?.price)
        }
    }
}

