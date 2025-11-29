package com.milen.realtimepricetracker.data.websocket

import com.milen.realtimepricetracker.data.network.model.SymbolDto
import java.math.BigDecimal

internal object StockData {
    val INITIAL_STOCKS = listOf(
        SymbolDto(
            id = "AAPL",
            name = "Apple",
            description = "Apple Inc. designs, manufactures, and markets smartphones, personal computers, tablets, wearables, and accessories worldwide.",
            price = BigDecimal("175.50")
        ),
        SymbolDto(
            id = "GOOG",
            name = "Google",
            description = "Google provides various products and platforms in the United States, Europe, the Middle East, Africa, the Asia-Pacific, Canada, and Latin America.",
            price = BigDecimal("140.25")
        ),
        SymbolDto(
            id = "TSLA",
            name = "Tesla",
            description = "Tesla, Inc. designs, develops, manufactures, leases, and sells electric vehicles, and energy generation and storage systems.",
            price = BigDecimal("245.80")
        ),
        SymbolDto(
            id = "AMZN",
            name = "Amazon",
            description = "Amazon.com, Inc. engages in the retail sale of consumer products and subscriptions in North America and internationally.",
            price = BigDecimal("155.30")
        ),
        SymbolDto(
            id = "MSFT",
            name = "Microsoft",
            description = "Microsoft Corporation develops, licenses, and supports software, services, devices, and solutions worldwide.",
            price = BigDecimal("420.15")
        ),
        SymbolDto(
            id = "NVDA",
            name = "NVIDIA",
            description = "NVIDIA Corporation provides graphics and compute and networking solutions in the United States, Taiwan, China, and internationally.",
            price = BigDecimal("485.90")
        ),
        SymbolDto(
            id = "META",
            name = "Meta Platforms",
            description = "Meta Platforms, Inc. engages in the development of products that help people connect and share with friends and family through mobile devices, personal computers, virtual reality headsets, and wearables worldwide.",
            price = BigDecimal("510.40")
        ),
        SymbolDto(
            id = "JPM",
            name = "JPMorgan Chase",
            description = "JPMorgan Chase & Co. operates as a financial services company worldwide. It operates through four segments: Consumer & Community Banking, Corporate & Investment Bank, Commercial Banking, and Asset & Wealth Management.",
            price = BigDecimal("195.75")
        ),
        SymbolDto(
            id = "V",
            name = "Visa",
            description = "Visa Inc. operates as a payments technology company worldwide. It facilitates commerce through the transfer of value and information among consumers, merchants, financial institutions, businesses, strategic partners, and government entities.",
            price = BigDecimal("280.60")
        ),
        SymbolDto(
            id = "JNJ",
            name = "Johnson & Johnson",
            description = "Johnson & Johnson researches and develops, manufactures, and sells various products in the healthcare field worldwide.",
            price = BigDecimal("165.20")
        ),
        SymbolDto(
            id = "WMT",
            name = "Walmart",
            description = "Walmart Inc. engages in the operation of retail, wholesale, and other units worldwide. The company operates through three segments: Walmart U.S., Walmart International, and Sam's Club.",
            price = BigDecimal("165.45")
        ),
        SymbolDto(
            id = "PG",
            name = "Procter & Gamble",
            description = "The Procter & Gamble Company provides branded consumer packaged goods to consumers in North and Latin America, Europe, the Asia Pacific, Greater China, India, the Middle East, and Africa.",
            price = BigDecimal("175.80")
        ),
        SymbolDto(
            id = "MA",
            name = "Mastercard",
            description = "Mastercard Incorporated, a technology company, provides transaction processing and other payment-related products and services in the United States and internationally.",
            price = BigDecimal("450.25")
        ),
        SymbolDto(
            id = "UNH",
            name = "UnitedHealth Group",
            description = "UnitedHealth Group Incorporated operates as a diversified healthcare company in the United States. It operates through four segments: UnitedHealthcare, OptumHealth, OptumInsight, and OptumRx.",
            price = BigDecimal("525.30")
        ),
        SymbolDto(
            id = "HD",
            name = "Home Depot",
            description = "The Home Depot, Inc. operates as a home improvement retailer. It operates The Home Depot stores that sell various building materials, home improvement products, lawn and garden products, and décor products.",
            price = BigDecimal("385.50")
        ),
        SymbolDto(
            id = "DIS",
            name = "Walt Disney",
            description = "The Walt Disney Company, together with its subsidiaries, operates as an entertainment company worldwide. The company operates through two segments: Disney Media and Entertainment Distribution; and Disney Parks, Experiences and Products.",
            price = BigDecimal("110.75")
        ),
        SymbolDto(
            id = "BAC",
            name = "Bank of America",
            description = "Bank of America Corporation, through its subsidiaries, provides banking and financial products and services for individual consumers, small and middle-market businesses, institutional investors, large corporations, and governments worldwide.",
            price = BigDecimal("38.90")
        ),
        SymbolDto(
            id = "ADBE",
            name = "Adobe",
            description = "Adobe Inc. operates as a diversified software company worldwide. It operates through three segments: Digital Media, Digital Experience, and Publishing and Advertising.",
            price = BigDecimal("580.40")
        ),
        SymbolDto(
            id = "CRM",
            name = "Salesforce",
            description = "salesforce.com, inc. provides customer relationship management technology that brings companies and customers together worldwide.",
            price = BigDecimal("275.60")
        ),
        SymbolDto(
            id = "NKE",
            name = "NIKE",
            description = "NIKE, Inc., together with its subsidiaries, designs, develops, markets, and sells athletic footwear, apparel, equipment, and accessories worldwide.",
            price = BigDecimal("105.25")
        ),
        SymbolDto(
            id = "XOM",
            name = "Exxon Mobil",
            description = "Exxon Mobil Corporation explores for and produces crude oil and natural gas in the United States and internationally.",
            price = BigDecimal("115.80")
        ),
        SymbolDto(
            id = "CVX",
            name = "Chevron",
            description = "Chevron Corporation, through its subsidiaries, engages in integrated energy, chemicals, and petroleum operations worldwide.",
            price = BigDecimal("155.40")
        ),
        SymbolDto(
            id = "COST",
            name = "Costco Wholesale",
            description = "Costco Wholesale Corporation, together with its subsidiaries, engages in the operation of membership warehouses in the United States, Puerto Rico, Canada, the United Kingdom, Mexico, Japan, Korea, Australia, Spain, France, Iceland, China, and Taiwan.",
            price = BigDecimal("850.50")
        ),
        SymbolDto(
            id = "AVGO",
            name = "Broadcom",
            description = "Broadcom Inc. designs, develops, and supplies various semiconductor devices with a focus on complex digital and mixed signal complementary metal oxide semiconductor based devices and analog III-V based products worldwide.",
            price = BigDecimal("1320.75")
        ),
        SymbolDto(
            id = "AMD",
            name = "Advanced Micro Devices",
            description = "Advanced Micro Devices, Inc. operates as a semiconductor company worldwide. The company operates in two segments, Computing and Graphics, and Enterprise, Embedded and Semi-Custom.",
            price = BigDecimal("145.90")
        )
    )
}
