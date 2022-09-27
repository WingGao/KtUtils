package com.github.winggao.kt.request

/**
 * 分页请求,隐藏细节，用于请求
 * User: Wing
 * Date: 2020/11/9
 */
open class WPage {
    var size: Long? = null
    var current: Long = 1
    var total: Long? = null
    var orders: List<PageOrderItem>? = null

    class PageOrderItem(
        var column: String? = null,
        var asc: Boolean = true
    ) {}
}

open class WPageT<T> : WPage() {
    var records: List<T>? = null
}
