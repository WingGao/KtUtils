package com.github.winggao.kt

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import org.junit.jupiter.api.Test
import java.util.*

class CoroutinesUtilsTest {
    suspend fun block(v: Int) {
        delay(1000)
        println("${Thread.currentThread()} ${Date()} $v")
    }

    @Test
    fun testLaunchChunk() {
        val ctx = newFixedThreadPoolContext(3, "test-chunk-ctx")
        val c = (1..10).map { it }
        launchParallel(ctx, c, 2, { block(it) })
        launchParallelIO(c, 2, { block(it) })
    }

    @Test
    fun testLaunchChunk2() {
        val ctx = newFixedThreadPoolContext(3, "test-chunk-ctx")
        val c = (1..100).map { it }
        launchParallel(ctx,
            channelFlow {
                c.chunked(10) {
                    val v = it.first()
                    println("${Thread.currentThread()} ${Date()} emit $v")
                    launch {
                        send(v)
                    }
                }
            }, 2, { block(it) })
    }
}
