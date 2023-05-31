package com.github.winggao.kt.mp

import cn.hutool.core.util.ReflectUtil
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper
import com.baomidou.mybatisplus.core.conditions.SharedString
import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.core.metadata.OrderItem
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper
import com.baomidou.mybatisplus.core.toolkit.StringPool
import com.baomidou.mybatisplus.extension.conditions.query.ChainQuery
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateChainWrapper
import com.baomidou.mybatisplus.extension.kotlin.KtUpdateWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.github.winggao.kt.request.WPage
import com.github.winggao.kt.request.WPageT
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.min
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

private fun getSqlSelectField(q: KtQueryWrapper<*>): SharedString {
    return ReflectUtil.getFieldValue(q, "sqlSelect") as SharedString
}


/**
 * KtQueryWrapper 支持String列名
 */
fun <T : Any> KtQueryWrapper<T>.addSelect(vararg columns: String): KtQueryWrapper<T> {
    if (columns.isNotEmpty()) {
        val sh = getSqlSelectField(this)
        if (sh.stringValue.isNullOrEmpty()) sh.stringValue = columns.joinToString(StringPool.COMMA)
        else sh.stringValue += StringPool.COMMA + columns.joinToString(StringPool.COMMA)
    }
    return this
}

fun <T : Any> KtUpdateWrapper<T>.setSql(column: KProperty1<T, *>, sql: String): KtUpdateWrapper<T> {
    this.setSql("${this.columnsToString(true, column)}=$sql")
    return this
}

fun <T : Any> KtUpdateChainWrapper<T>.setSql(column: KProperty1<T, *>, sql: String): KtUpdateChainWrapper<T> {
    (this.wrapper as KtUpdateWrapper<T>).setSql(column, sql)
    return this
}

fun <T> ChainQuery<T>.oneOrNull(): T? {
    val out = this.oneOpt()
    if (out.isPresent) return out.get()
    return null
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

object MpUtils {
    // key1=table名 key2=propName val=colName 保存当前的所有表的类型
    private val tableColumnMap = ConcurrentHashMap<String, Map<String, String>>()

    /**
     * 优化select
     * @param dtoPropName 如果为null，则表示直接根据sqlTableAlias选择propNames；如果不为null，则表示选出的props属于dtoPropName
     * @param propNames 要选择的字段，为空则全部
     */
    fun <C : Any> selectToPropSql(
        dtoPropName: String?,
        propTableClass: KClass<C>,
        sqlTableAlias: String,
        vararg propNames: KMutableProperty1<C, *>
    ): String {
        val tableInfo = TableInfoHelper.getTableInfo(propTableClass.java) ?: return ""
        // 获取缓存
        var colMap = tableColumnMap[tableInfo.tableName]
        if (colMap == null) {
            colMap = tableInfo.fieldList.associate { v -> v.property to v.column }.toMutableMap()
            colMap[tableInfo.keyProperty] = tableInfo.keyColumn //添加主键
            tableColumnMap[tableInfo.tableName] = colMap
        }
        val sqlB = StringBuilder()
        var propNameList = propNames.toList()
        if (propNames.isEmpty()) {
            propNameList = propTableClass.memberProperties.toList() as List<KMutableProperty1<C, *>>
        }
        propNameList.forEachIndexed { idx, p ->
            val col = colMap[p.name] ?: return@forEachIndexed
            if (idx > 0) sqlB.append(",")
            if (dtoPropName == null) sqlB.append("$sqlTableAlias.`${col}`")
            else sqlB.append("$sqlTableAlias.`${col}` as `${dtoPropName}.${p.name}`")
        }
        return sqlB.toString()
    }
}