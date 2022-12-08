package org.course.stockstrader

import org.springframework.boot.SpringApplication.run
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableR2dbcRepositories
open class StockExchangeApplication

fun main(args: Array<String>) {
    run(StockExchangeApplication::class.java, *args)
}
