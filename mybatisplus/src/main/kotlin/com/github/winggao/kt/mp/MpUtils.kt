package com.github.winggao.kt.mp

import cn.hutool.core.util.ReflectUtil
import com.baomidou.mybatisplus.core.conditions.SharedString
import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.core.metadata.OrderItem
import com.baomidou.mybatisplus.core.toolkit.StringPool
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.github.winggao.kt.request.WPage
import com.github.winggao.kt.request.WPageT
import kotlin.math.min

private fun getSqlSelectField(q: KtQueryWrapper<*>): SharedString {
    return ReflectUtil.getFieldValue(q, "sqlSelect") as SharedString
}

/**
 * KtQueryWrapper 支持String列名
 */
fun <T : Any> KtQueryWrapper<T>.select(vararg columns: String): KtQueryWrapper<T> {
    if (columns.isNotEmpty()) {
        getSqlSelectField(this).stringValue = columns.joinToString(StringPool.COMMA)
    }
    return this
}

//region wpage
fun <T> IPage<T>.toW(): WPageT<T> {
    return WPageT<T>().also {
        it.current = this.current
        it.size = this.size
        it.total = this.total
        it.records = this.records
    }
}

fun <T> WPage.toMp(maxSize: Long = 100): Page<T> {
    return Page<T>().also {
        if (this.size != null) it.size = this.size!!
        it.size = min(maxSize, it.size)
        it.current = this.current
    }
}

/**
 * 转换排序，如果不在map里，则忽律
 * key=客户端的字段, value=目标字段,如果为空则使用key作为排序字段
 */
fun WPage.getMpOrders(allowMap: Map<String, String>): List<OrderItem> {
    val res = ArrayList<OrderItem>()
    if (this.orders != null) {
        this.orders!!.forEach {
            var toF = allowMap.get(it.column)
            if (toF != null) {
                if (toF == "") toF = it.column
                res.add(OrderItem(toF, it.asc))
            }
        }
    }
    return res
}

//endregion