package com.github.winggao.kt

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlin.coroutines.CoroutineContext

fun launchIO(block: suspend CoroutineScope.() -> Unit): Job {
    return GlobalScope.launch(Dispatchers.IO) {
        block()
    }
}

/**
 * 并行运行
 */
fun <T> launchParallelIO(c: Collection<T>, chunkSize: Int, block: suspend CoroutineScope.(v: T) -> Unit) {
    return launchParallel(Dispatchers.IO, c, chunkSize, block)
}

/**
 * 并行运行
 * @param c 输入集
 * @param chunkSize 并行大小
 * @param block 处理方法
 */
fun <T> launchParallel(context: CoroutineContext, c: Collection<T>, chunkSize: Int, block: suspend CoroutineScope.(v: T) -> Unit) {
    val ch = Channel<T>()
    runBlocking {
        launch(context) {
            c.forEach { ch.send(it) }  //放到channel
            ch.close()
        }
        (1..chunkSize).map {
            launch(context) {
                for (v in ch) block(v)
            }
        }.joinAll()
    }
}
