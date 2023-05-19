package com.github.winggao.kt.rd

import org.redisson.api.RLock
import java.util.concurrent.TimeUnit

val LockFail = Exception("LockFail")
fun <T> RLock.lockRun(fn: () -> T, leaseTime: Long, unit: TimeUnit): Result<T> {
    this.lock(leaseTime, unit)
    val r = runCatching(fn)
    this.unlock()
    return r
}


fun <T> RLock.tryLockRun(fn: () -> T, leaseTime: Long, unit: TimeUnit): Result<T> {
    if (!this.tryLock(leaseTime, unit)) return Result.failure(LockFail)
    val r = runCatching(fn)
    this.unlock()
    return r
}
