package com.github.winggao.kt

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.CoroutineContext

fun launchIO(block: suspend CoroutineScope.() -> Unit): Job {
    return GlobalScope.launch(Dispatchers.IO) {
        block()
    }
}

/**
 * 并行运行
 */
fun <T> launchParallelIO(c: Collection<T>, parallelNum: Int, block: suspend CoroutineScope.(v: T) -> Unit) {
    return launchParallel(Dispatchers.IO, c, parallelNum, block)
}

fun <T> launchParallelIO(f: Flow<T>, parallelNum: Int, block: suspend CoroutineScope.(v: T) -> Unit) {
    return launchParallel(Dispatchers.IO, f, parallelNum, block)
}

/**
 * 并行运行
 * @param c 输入集
 * @param parallelNum 并行大小
 * @param block 处理方法
 */
fun <T> launchParallel(context: CoroutineContext, c: Collection<T>, parallelNum: Int, block: suspend CoroutineScope.(v: T) -> Unit) {
    val ch = Channel<T>()
    runBlocking {
        launch(context) {
            c.forEach { ch.send(it) }  //放到channel
            ch.close()
        }
        (1..parallelNum).map {
            launch(context) {
                for (v in ch) block(v)
            }
        }.joinAll()
    }
}
fun <T> launchParallel(context: CoroutineContext, f: Flow<T>, parallelNum: Int, block: suspend CoroutineScope.(v: T) -> Unit) {
    val ch = Channel<T>()
    runBlocking {
        launch(context) {
            f.collect { ch.send(it) } //放到channel
            ch.close()
        }
        (1..parallelNum).map {
            launch(context) {
                for (v in ch) block(v)
            }
        }.joinAll()
    }
}

