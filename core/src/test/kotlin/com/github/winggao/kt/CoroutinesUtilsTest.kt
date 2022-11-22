package com.github.winggao.kt

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.newFixedThreadPoolContext
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
}