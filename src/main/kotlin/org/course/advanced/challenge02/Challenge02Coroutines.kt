package org.course.advanced.challenge02

import kotlinx.coroutines.delay


class CurrencyService(private val latency: Long, private val exchangeRates: Map<String, Double>) {

    fun getCurrencyBlocking(currency: String): Double {
        Thread.sleep(latency)
        return exchangeRates.getOrDefault(currency, 0.0)
    }

    /**
     * Exercise A: implement getCurrency similar to getCurrencyBlocking but then without blocking
     * a Thread. Make it suspend and do not use sleep to simulate a delay but instead use...
     */
    suspend fun getCurrency(currency: String): Double {
        delay(latency)
        return exchangeRates.getOrDefault(currency, 0.0)
    }


    companion object {
        val USD = "USD"
    }
}