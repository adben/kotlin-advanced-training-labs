package org.course.stockstrader.coroutines.domain


data class StockOrderDto(val symbol: String,
                         val count: Int)

data class StockQuoteDto(val symbol: String,
                         val currentPrice: Double)