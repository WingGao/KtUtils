package com.github.winggao.kt.rd

import org.redisson.api.RedissonClient
import java.util.concurrent.TimeUnit

class RedisCache<T : Any>(val client: RedissonClient, val key: String, val onFetch: () -> T?) {
    private val rd by lazy { client.getBucket<T>(key, TypedFastJsonCodec()) }
    private val lock = client.getLock("$key:lock")
    var ttlV: Long = 1
    var ttlU: TimeUnit = TimeUnit.HOURS

    fun expire(ttl: Long, unit: TimeUnit): RedisCache<T> {
        ttlV = ttl
        ttlU = unit
        return this
    }

    fun value(): T? {
        lock.tryLock(10, 10, TimeUnit.MINUTES)
        var res: T? = null
        kotlin.runCatching {
            res = rd.get()
            if (res == null) {
                res = onFetch()
                if (res != null) rd.set(res, ttlV, ttlU)
            }
        }
        lock.unlock()
        return res
    }
}