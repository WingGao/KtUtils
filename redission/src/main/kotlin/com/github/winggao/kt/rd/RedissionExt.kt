package com.github.winggao.kt.rd

import org.redisson.api.RLock
import java.util.concurrent.TimeUnit


fun <T> RLock.lockRun(fn: () -> T, leaseTime: Long, unit: TimeUnit): Result<T> {
    this.lock(leaseTime, unit)
    val r = runCatching(fn)
    this.unlock()
    return r
}
