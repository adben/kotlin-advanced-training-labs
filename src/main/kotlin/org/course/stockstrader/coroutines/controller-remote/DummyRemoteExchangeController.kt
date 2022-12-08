package org.course.stockstrader.coroutines.`controller-remote`

import org.springframework.web.bind.annotation.*
import org.course.stockstrader.coroutines.domain.StockQuoteDto
import org.course.stockstrader.reactor.service.JExchangeServiceEuronext
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

@RestController
open class DummyRemoteExchangeController {

    @GetMapping("/quotes")
    @ResponseBody
    suspend fun getQuote(@RequestParam("symbol") symbol:String, @RequestParam("exchange") exchange:String): StockQuoteDto =
            StockQuoteDto(symbol, get(exchange, symbol) ?: Random.nextDouble(50.0, 10000.0))

    companion object {
        private val  memRepo:ConcurrentHashMap<String, Double> = ConcurrentHashMap()
        fun clearMemRepo() = memRepo.clear()
        fun add(exchange: String, symbol: String, price:Double) = memRepo.put("$exchange-$symbol", price)
        fun get(exchange: String, symbol: String):Double? = memRepo["$exchange-$symbol"]
    }
}
