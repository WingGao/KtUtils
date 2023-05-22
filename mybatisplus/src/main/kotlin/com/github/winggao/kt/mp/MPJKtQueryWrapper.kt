package com.github.winggao.kt.mp

import com.baomidou.mybatisplus.core.enums.SqlKeyword
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper
import com.github.winggao.kt.reflect.ReflectExt
import com.github.yulichang.query.MPJQueryWrapper
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.memberProperties

/***
 * 用于支持Kotlin的MPJQueryWrapper
 * 1. 支持Kotlin的Entity
 * 2. 支持TableLogic自动补全
 */
@Deprecated("WIP")
class MPJKtQueryWrapper<T : Any> : MPJQueryWrapper<T> {
    private val tableAlias = HashMap<Class<*>, String>()
    private val baseKClass: KClass<T>
    private val propCache = HashMap<KProperty<*>, KPropInfo>() //属性对应的表

    /**
     * @param baseKC 底表的KClass
     */
    constructor(baseKC: KClass<T>) : super() {
        this.baseKClass = baseKC
        this.entityClass = baseKC.java //传入底表
        addAlias(baseKC, "t")

        //基础表
        val table = TableInfoHelper.getTableInfo(this.entityClass)
        //补全TableLogic
        if (table.isWithLogicDelete) this.eq(
            "t.${table.logicDeleteFieldInfo.column}",
            table.logicDeleteFieldInfo.logicNotDeleteValue
        )
    }

    fun addAlias(kc: KClass<*>, alias: String) {
        if (tableAlias.containsKey(kc.java)) return
        tableAlias[kc.java] = alias
        val table = TableInfoHelper.getTableInfo(kc.java)
        val propMap = kc.memberProperties.map { it.name to it }.toMap()
        //添加keyProp
        propMap.get(table.keyProperty)?.let {
            propCache[it] = KPropInfo(it, kc, table.keyColumn)
        }
        //普通field
        table.fieldList.forEach {
            val prop = propMap[it.property] ?: return@forEach
            propCache[prop] = KPropInfo(prop, kc, it.column, it)
        }
    }

    fun columnName(p: KProperty1<*, *>, withTable: Boolean = true): String {
        val propInfo = propCache[p]!!
        return if (withTable) "`${propInfo.table.tableName}`.`${propInfo.column}`" else "`${propInfo.column}`"
    }

    fun valueToStr(v: Any): String {
        return when (v) {
            is KProperty1<*, *> -> columnName(v)
            else -> v.toString()
        }
    }

    fun <E : Any> leftJoin(t2: KClass<E>, onMS: (MPJCompare) -> Unit, alias: String? = null): MPJKtQueryWrapper<T> {
        //设置别名
        var tAlias = alias
        if (tAlias.isNullOrEmpty()) tAlias = "t${tableAlias.size + 1}"
        addAlias(t2, tAlias)
        //获取表原名
        val table = TableInfoHelper.getTableInfo(t2.java)
        val sb = StringBuilder("${table.tableName} `${tAlias}` ON ")
        val cmp = MPJCompare(this)
        onMS(cmp)
        if (cmp.conditions.isEmpty()) throw Exception("onMS至少一个条件")
        //补全TableLogic
        if (table.isWithLogicDelete) sb.append("${tAlias}.${table.logicDeleteFieldInfo.column}=${table.logicDeleteFieldInfo.logicNotDeleteValue} AND ")
        cmp.conditions.forEachIndexed { index, c ->
            if (index > 0) sb.append(" AND ")
            sb.append("${columnName(c.column)}${c.op.sqlSegment}${valueToStr(c.value)}")
        }
        this.leftJoin(sb.toString())
        return this
    }

    fun where(block: (MPJCompare) -> Unit): MPJKtQueryWrapper<T> {
        val cmp = MPJCompare(this)
        block(cmp)
        cmp.conditions.forEach { c ->
            this.addCondition(true, columnName(c.column), c.op, c.value)
        }
        return this
    }

    fun ktSelect(entity: KClass<*>): MPJKtQueryWrapper<T> {
        val baseFieldMap = this.baseKClass.memberProperties.map { it.name to it }.toMap()
        entity.memberProperties.forEach {
            //先匹配底表
            if (it.name in baseFieldMap) {
                this.select(columnName(baseFieldMap[it.name]!!))
            }
        }
        return this
    }

    class KPropInfo(
        val kProp: KProperty<*>,
        val parent: KClass<*>,
        val column: String,
        val fieldInfo: TableFieldInfo? = null
    ) {
        val table by lazy { TableInfoHelper.getTableInfo(parent.java) }
    }
}

class MPJCompare(val wrapper: MPJKtQueryWrapper<*>) {
    val conditions = ArrayList<CompareItem<*, *>>()
    fun eq(column: KProperty1<*, *>, value: Any): MPJCompare {
        conditions.add(CompareItem(SqlKeyword.EQ, column, value))
        return this
    }

    fun addCondition(column: KProperty<*>, sqlKeyword: SqlKeyword, v: Any): MPJCompare {
//        wrapper.eq()
        return this
    }

    class CompareItem<T, V>(val op: SqlKeyword, val column: KProperty1<T, V>, val value: Any)
}
