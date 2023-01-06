package com.github.winggao.kt.mp

import com.baomidou.mybatisplus.core.conditions.AbstractWrapper
import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.extension.conditions.query.ChainQuery
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.github.winggao.kt.request.RangeT

/**
 * 将RangeT的操作映射到MBP
 */
fun <T, R, Children : AbstractWrapper<T, R, Children>, G> AbstractWrapper<T, R, Children>.betweenRange(column: R, r: RangeT<G>) {
    if (r.begin != null) {
        if (r.includeLeft) this.ge(column, r.begin)
        else this.gt(column, r.begin)
    }
    if (r.end != null) {
        if (r.includeRight) this.le(column, r.end)
        else this.lt(column, r.end)
    }
}


fun <T> ChainQuery<T>.chunk(chunkSize: Long, action: (IPage<T>) -> Unit) {
    if (chunkSize <= 0) throw Error("chunkSize必须大于0")
    var page = Page<T>(0, chunkSize)
    page.isSearchCount = false //不查询count
    while (true) {
        val res = this.page(page)
        action(res)
        if (res.records.size < chunkSize) break
        page = Page<T>(res.current + 1, chunkSize) //下一页
    }
}
