package org.course.advanced.challenge04

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import java.util.concurrent.atomic.AtomicInteger

class Challenge04ChannelsTest {

    /**
     * Exercise A:
     * Let's start communicating with Coroutines via a Channel.
     * In this exercise you have to send the message 'hi' to the given channel.
     * Tip: if the test does not complete you might have to do the sending in a separate Coroutine. Any idea why?
     */
    @Test
    @Timeout(2)
    fun `Exercise A should send a message to a channel`() = runBlocking {
        val channel = Channel<String>()
        //TODO: send the message 'hi' to the given channel. Tip: if the test does not complete you might have to do the sending in a separate Coroutine. Any idea why?

        channel.receive() shouldBe "hi"
    }


    /**
     * Exercise B:
     * Now let's look at the consuming side of a Channel.
     * In this exercise you have to implement logic to consume all messages sent to the given channel
     * using consumeEach. Once all messages are consumed the channel needs to be closed.
     * Bonus question: what would happen if sending messages in the code below is not
     * triggered in a separate Coroutine?
     */
    @Test
    @Timeout(2)
    fun `Exercise B should consume all messages of the channel using consumeEach and close the channel when all are received using close`() = runBlocking {
        val channel = Channel<String>()
        val messages = listOf("msg1", "msg2")
        val counter = AtomicInteger(0)
        messages.forEach { msg ->
            launch { channel.send(msg).also { println("Sent $msg") } }
        }


        //TODO: implement logic to consume all messages sent to this channel using consumeEach.
        //Make sure you increment the counter for each message you have consumed.
        //Once all messages are consumed the channel needs to be closed.


        channel.isClosedForReceive shouldBe true
        channel.isClosedForSend shouldBe true
        counter.get() shouldBe messages.size

        //Bonus question: what would happen if sending messages in the above statement is not triggered in a separate Coroutine?
    }


    /**
     * Exercise C:
     * In the previous exercises a sent message could only be consumed by one Coroutine.
     * In this exercise you will use a BroadcastChannel so that a sent message can be consumed by multiple subscribed Coroutines.
     * Implement logic to create 10 subscribers for a BroadcastChannel using consumeEach and start consuming messages.
     * For each consumed message call the consumed(...) method.
     */
    @Test
    @Timeout(5)
    fun `Exercise C create 10 subscribers for a BroadcastChannel and consume all messages using consumeEach calling consumed upon receiving a msg`() = runBlocking {
        val broadcastChannel = BroadcastChannel<String>(Channel.BUFFERED)
        val counter = AtomicInteger(0)

        suspend fun consumed(subscriberId:Int, msg: String) {
            println("Subscriber=$subscriberId Consumed=$msg")
            counter.incrementAndGet()
            delay(100)
        }

        //TODO: implement logic to create 10 subscribers for a BroadcastChannel using consumeEach and start consuming messages. For each consumed message call the consumed(...) method

        //start sending
        listOf("msg1", "msg2").forEach { msg ->
            launch { broadcastChannel.send(msg).also { println("Sent $msg") } }
        }
        //we expect the count to be 20: 10 subscribers consuming 2 messages each
        //test will not complete if channel is still open
        closeChannelWhenCountReached(broadcastChannel, counter, expectedCount = 20)
    }

    companion object {
        private tailrec suspend fun <T> closeChannelWhenCountReached(channel: BroadcastChannel<T>, counter: AtomicInteger, expectedCount: Int) {
            if (counter.get() == expectedCount) channel.close() else {
                delay(200)
                closeChannelWhenCountReached(channel, counter, expectedCount)
            }
        }
    }
}
