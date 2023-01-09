package com.github.winggao.kt

import java.util.concurrent.ConcurrentLinkedQueue

/**
 * 可加载队列
 */
open class LoadQueue<T>(var cacheSize: Int, var loadAction: (size: Int) -> Collection<T>) {
    var noResult = false
    private val cacheQueue = ConcurrentLinkedQueue<T>()

    /**
     * 移除队首
     */
    fun poll(): T? {
        synchronized(this) {
            if (cacheQueue.isEmpty() && !noResult) {
                loadAction(cacheSize).let {
                    if (it.isEmpty()) noResult = true
                    else cacheQueue.addAll(it)
                }
            }
        }
        return cacheQueue.poll()
    }
}