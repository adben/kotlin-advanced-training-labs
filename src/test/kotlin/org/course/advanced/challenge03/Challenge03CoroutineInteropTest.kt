package org.course.advanced.challenge03

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.course.advanced.challenge03.AsyncCurrencyService.Companion.CHF
import org.course.advanced.challenge03.AsyncCurrencyService.Companion.INEXISTANT_CURRENCY
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis


class Challenge03CoroutineInteropTest {

    val bankC = AsyncCurrencyService(500, mapOf(CHF to 109.12))
    val bankD = AsyncCurrencyService(1000, mapOf(CHF to 110.2))
    val bankE = AsyncCurrencyService(1500, mapOf(CHF to 111.3))
    val banks = listOf(bankC, bankD, bankE)


    /**
     * Exercise A:
     * In this exercise you will use existing glue code that glues reactive library calls into Coroutines.
     * Take a look at @see AsyncCurrencyService#getCurrencyFuture. It uses a CompletableFuture with an artificial latency to return a currency.
     * For this exercise calculate the minimum conversion rate by calling the getCurrencyFuture(CHF) of all banks.
     * Use coroutines and its existing helper method kotlinx.coroutines.future.await() on CompletableFuture to lift the CompletableFuture into the Coroutine universe.
     * Provide the implementation in this test.
     */
    @Test
    fun `Exercise A calculate the minimum conversion rate by calling the getCurrencyFuture(CHF) of all banks with coroutines and its helper method await on CompletableFuture`() {
        runBlocking {
            //check de parallelism
            val minRate = banks.map { it.getCurrencyFuture(CHF) }.minOfOrNull { it.await() }
            minRate shouldBe 109.12
        }
    }

    /**
     * Exercise B:
     * In this exercise you will write some glue code yourself, which bridges an asynchronous method call to a Coroutine.
     * Take a look at @see AsyncCurrencyService#getCurrencyAsync. It uses a callback interface @see AsyncCurrencyCallback to either set a currency
     * or an exception. The approach can lead to the well-known callback-hell, which is hard to reason about and test. With Coroutines we can do
     * much better: for this exercise implement the extension method AsyncCurrencyService#getCurrencySuspended.
     * Forward the call to AsyncCurrencyService#getCurrencyAsync and map the result of the @see AsyncCurrencyCallback to the Continuation, which
     * can be accessed with some special 'Coroutine glue code'.
     *
     * Tip: The Coroutine glue code involves the helper method suspendCoroutine {...} which gives you access to the underlying Continuation.
     * With the underlying Continuation a success or failure result can be set.
     * Make this test succeed.
     */
    @Test
    fun `Exercise B implement the getCurrencySuspended() method that calls getCurrencyAsync and maps the Callback to the Coroutines Continuation using the suspendCoroutine helper`() {
        val coroutinesMs = measureTimeMillis {
            runBlocking {
                //success results should work
                val averageRateOfAllBanks = banks.map { async{it.getCurrencySuspended(CHF)} }.awaitAll().average()
                averageRateOfAllBanks shouldBe listOf(109.12, 110.2, 111.3).average()

                //failure results should work too
                shouldThrow<Exception> { bankC.getCurrencySuspended(INEXISTANT_CURRENCY) }
            }
        }
        coroutinesMs.toDouble() shouldBe 2000.toDouble().plusOrMinus(400.0)
    }
}

