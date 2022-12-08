package org.course.stockstrader.coroutines

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.runBlocking
import org.course.stockstrader.coroutines.domain.Stock
import org.course.stockstrader.coroutines.repository.StocksRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension


@SpringBootTest
@ExtendWith(SpringExtension::class)
class StocksRepositoryTest @Autowired constructor(
        val stocksRepository: StocksRepository
) {

    @Test
    fun `should crud new stock`() = runBlocking {
        //create
        val newStock = stocksRepository.save(Stock(symbol = GOOG, price = 124.12))
        var foundStock = stocksRepository.findById(newStock.id!!)
        foundStock shouldBe newStock

        //update
        val updatedStock = newStock.copy(price = 250.12)
        stocksRepository.save(updatedStock)
        foundStock = stocksRepository.findById(newStock.id!!)
        foundStock shouldBe updatedStock

        //delete
        stocksRepository.delete(updatedStock)
        stocksRepository.findById(newStock.id!!) shouldBe null
    }


    companion object {
        const val AAPL = "AAPL"
        const val MSFT = "MSFT"
        const val AMZN = "AMZN"
        const val GOOG = "GOOG"
    }
}