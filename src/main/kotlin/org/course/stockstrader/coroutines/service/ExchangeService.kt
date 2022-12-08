package org.course.stockstrader.coroutines.service

import kotlinx.coroutines.reactor.awaitSingle
import org.course.stockstrader.coroutines.domain.StockQuoteDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

abstract class ExchangeService(val baseUrl: String, val exchangeId: String) {

    private val client by lazy { WebClient.create(baseUrl) }

    /**
     * Challenge 6 - Exercise B
     * Take a look at the reactive @see JExchangeService#getStockQuote Java implementation,
     * which makes use of Mono<T> as reactive abstraction for a single result fetched by a remote service.
     * In this exercise you have to implement the getStockQuote method in a reactive way
     * using Coroutines. So you should create a suspend method that does not return a Mono<T> but a T right away.
     * Tip: use the WebClient to do the REST call. Convert the resulting Mono<T> with the
     * glue method awaitBody<T> into a suspended method call.
     * Make the corresponding test in @see Challenge06ServiceDaoTest pass.
     */
    //TODO: implement the getStockQuote method
    suspend fun getStockQuote(stockSymbol: String) =
        client.get()
            .uri("/quotes?symbol=$stockSymbol&exchange=$exchangeId")
            .retrieve()
            .bodyToMono<StockQuoteDto>(StockQuoteDto::class.java)
            .awaitSingle()

}

@Component
open class ExchangeServiceNasdaq(@Value("\${remote.service.url}") baseUrl: String) : ExchangeService(baseUrl, "nasdaq")

@Component
open class ExchangeServiceEuronext(@Value("\${remote.service.url}") baseUrl: String) :
    ExchangeService(baseUrl, "euronext")

@Component
open class ExchangeServiceSix(@Value("\${remote.service.url}") baseUrl: String) : ExchangeService(baseUrl, "six")