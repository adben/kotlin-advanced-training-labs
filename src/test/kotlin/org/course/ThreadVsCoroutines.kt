package org.course

import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Thread.sleep
import kotlin.concurrent.thread

fun main() {
    runBlocking {
        val channel = BroadcastChannel<String>(20)
        launch {
            delay(600)
            channel.openSubscription()
                .consumeEach { println("Turtle 1 meets: ${it}") }
        }
        launch {
            delay(600)
            channel.openSubscription()
                .consumeEach { println("Turtle 2 meets: ${it}") }
        }
        launch {
            delay(600)
            channel.openSubscription()
                .consumeEach { println("Turtle 3 meets: ${it}") }
        }
        launch {
            channel.send("Rabbit")
            println("send Rabbit")
            channel.send("Rabbot")
            println("send Rabbot")
            delay(700)
            channel.send("Rabbyte")
            println("send Rabbyte")
//            delay(500)
//            channel.close()
        }

    }





}