package org.course.stockstrader.challenge07

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.runBlocking
import org.course.stockstrader.coroutines.`controller-remote`.DummyRemoteExchangeController
import org.course.stockstrader.coroutines.domain.Stock
import org.course.stockstrader.coroutines.domain.StockQuoteDto
import org.course.stockstrader.coroutines.repository.StocksRepository
import org.course.stockstrader.coroutines.service.ExchangeService
import org.course.stockstrader.coroutines.service.ExchangeServiceEuronext
import org.course.stockstrader.coroutines.service.ExchangeServiceNasdaq
import org.course.stockstrader.coroutines.service.ExchangeServiceSix
import org.course.stockstrader.utils.prepareTestData
import org.course.stockstrader.utils.reAssignVal
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension::class)
@AutoConfigureWebTestClient
class Challenge07ControllerTest @Autowired constructor(val stocksRepository: StocksRepository,
                                                       val exchangeServiceNasdaq: ExchangeServiceNasdaq,
                                                       val exchangeServiceEuronext: ExchangeServiceEuronext,
                                                       val exchangeServiceSix: ExchangeServiceSix,
                                                       val webTestClient: WebTestClient,
                                                       @LocalServerPort val localPort: Int) {

    val exchanges = listOf(exchangeServiceNasdaq, exchangeServiceEuronext, exchangeServiceSix)
    lateinit var initialStocks:List<Stock>

    @BeforeEach
    fun setup() = runBlocking {
        initialStocks = stocksRepository.prepareTestData()
        exchanges.forEach { it.reAssignVal(ExchangeService::baseUrl) { it.replace("8081", localPort.toString()) } }
    }


    /**
     * Exercise A:
     * For instructions go to @see StockTraderController#getStock
     */
    @Test
    fun `Exercise A should get stock by id`(): Unit  {
        val stock = initialStocks.first()
        webTestClient.get().uri("/stocks/${stock.id}").exchange().apply {
            expectStatus().isOk
            expectBody<Stock>().returnResult().responseBody?.symbol shouldBe stock.symbol
        }
    }

    /**
     * Exercise B:
     * For instructions go to @see StockTraderController#stockBySymbol
     */
    @Test
    fun `Exercise B should get stock by symbol`(): Unit = runBlocking {
        webTestClient.get().uri("/stock?symbol=$GOOG").exchange().apply {
            expectStatus().isOk
            expectBody<Stock>().returnResult().responseBody?.symbol shouldBe GOOG
        }
    }

    /**
     * Exercise C:
     * For instructions go to @see StockTraderController#upsertStock
     */
    @Test
    fun `Exercise C should upsert stock`(): Unit = runBlocking {
        val newStock = Stock(symbol = NEWS, price = 123.12)
        fun testPost(stock: Stock): Stock? =
                webTestClient.post().uri("/stocks").bodyValue(stock).exchange().run {
                    expectStatus().isOk
                    expectBody<Stock>().returnResult().responseBody.apply {
                        this?.price shouldBe stock.price
                        this?.symbol shouldBe stock.symbol
                        this?.id shouldNotBe null
                    }
                }
        //new stock
        val insertedStock = testPost(newStock)!!
        //update stock
        val toUpdate = insertedStock.copy(price = 345.23)
        testPost(toUpdate)
    }


    /**
     * Exercise D:
     * For instructions go to @see StockTraderController#bestQuote
     */
    @Test
    fun `Exercise D should fetch best quote for a stock in parallel`(): Unit = runBlocking {
        DummyRemoteExchangeController.apply {
            clearMemRepo()
            exchanges.withIndex().forEach { (idx, exchange) -> add(exchange.exchangeId, GOOG, idx.toDouble()) }
        }
        webTestClient.get().uri("/stocks/quote?symbol=$GOOG").exchange().run {
            expectStatus().isOk
            expectBody<StockQuoteDto>().returnResult().responseBody.apply {
                this?.currentPrice shouldBe DummyRemoteExchangeController.get(exchanges.first().exchangeId, GOOG)
            }
        }
    }


    companion object {
        const val NEWS = "NEWS"
        const val GOOG = "GOOG"
        const val AAPL = "AAPL"
    }
}
