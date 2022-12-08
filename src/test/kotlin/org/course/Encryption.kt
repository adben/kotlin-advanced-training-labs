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
   val text = "Hallo Quinn"

    val enc = text.map { it + 1 }.joinToString("")

    println(enc)

    val dec = enc.map { it - 1 }.joinToString("")
    println(dec)



}