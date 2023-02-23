package com.github.winggao.kt.mp

import cn.hutool.core.util.ReflectUtil
import com.baomidou.mybatisplus.core.conditions.SharedString
import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.github.yulichang.base.service.MPJJoinService
import com.github.yulichang.interfaces.MPJBaseJoin
import com.github.yulichang.query.MPJQueryWrapper
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

/**
 * 清除select信息
 */
fun <T> MPJQueryWrapper<T>.clearSelect() {
    (ReflectUtil.getFieldValue(this, "selectColumns") as MutableList<String>).clear()
    (ReflectUtil.getFieldValue(this, "sqlSelect") as SharedString).toNull()
}

/**
 * 根据prop选择
 */
fun <T, C : Any> MPJQueryWrapper<T>.select(tbClass: KClass<C>, tbAlias: String, vararg props: KProperty1<*, C>) {

}

/**
 * 批量chunk操作
 * @param action: true=继续，false=停止循环
 */
fun <E : Any, R> MPJJoinService<E>.selectJoinListPageChunk(chunkSize: Long, outClz: Class<R>, wrapper: MPJBaseJoin<E>, action: (IPage<R>) -> Boolean) {
    var page = Page<R>(0, chunkSize)
    page.isSearchCount = false //不查询count
    while (true) {
        val res = this.selectJoinListPage(page, outClz, wrapper)
        if (res.records.isEmpty()) break
        if (!action(res)) break
        if (res.records.size < chunkSize) break
        page = Page<R>(res.current + 1, chunkSize) //下一页
        page.isSearchCount = false
    }
}