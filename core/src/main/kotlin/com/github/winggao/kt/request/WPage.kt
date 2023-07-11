package com.github.winggao.kt.request

import kotlin.math.max

/**
 * 分页请求,隐藏细节，用于请求
 * User: Wing
 * Date: 2020/11/9
 */
open class WPage {
    private val defaultSize = 10L
    var size: Long? = null

    /**
     * 当前页，从1开始
     */
    var current: Long = 1
    var total: Long? = null
    var orders: List<PageOrderItem>? = null

    class PageOrderItem(
        var column: String? = null,
        var asc: Boolean = true
    ) {}

    fun currentSize(): Long {
        return max(size ?: defaultSize, 1)
    }

    /**
     * 从0开始的index
     */
    fun beginIndex(): Long {
        return max(0, (current - 1) * currentSize())
    }

    fun endIndex(): Long {
        return beginIndex() + (size ?: defaultSize) - 1
    }
}

open class WPageT<T> : WPage() {
    var records: List<T>? = null
}
