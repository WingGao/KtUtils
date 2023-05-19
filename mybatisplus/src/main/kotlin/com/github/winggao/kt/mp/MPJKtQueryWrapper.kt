package com.github.winggao.kt.mp

import com.baomidou.mybatisplus.core.enums.SqlKeyword
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper
import com.github.winggao.kt.reflect.ReflectExt
import com.github.yulichang.query.MPJQueryWrapper
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMembers
import kotlin.reflect.full.memberProperties

//TODO
@Deprecated("WIP")
class MPJKtQueryWrapper<T : Any> : MPJQueryWrapper<T> {
    private val tableAlias = HashMap<Class<*>, String>()
    private val baseKClass:KClass<T>

    /**
     * @param baseKC 底表的KClass
     */
    constructor(baseKC: KClass<T>) : super() {
        this.baseKClass = baseKC
        this.entityClass = baseKC.java //传入底表
        tableAlias[entityClass] = "t"
    }

    fun columnName(p: KProperty1<*, *>, withTable: Boolean = true): String {
        var cName = p.name
        return if (withTable) "`${tableAlias[ReflectExt.getOwnerClass(p as KProperty1<Any, *>).java]}`.`${cName}`" else "`${cName}`"
    }

    fun valueToStr(v: Any): String {
        return when (v) {
            is KProperty1<*, *> -> columnName(v)
            else -> v.toString()
        }
    }

    fun <E : Any> leftJoin(t2: KClass<E>, alias: String? = null, onMS: (MPJCompare) -> Unit): MPJKtQueryWrapper<T> {
        //设置别名
        var tAlias = alias
        if (tAlias.isNullOrEmpty()) tAlias = "t${tableAlias.size + 1}"
        tableAlias[t2.java] = tAlias
        //获取表原名
        val table = TableInfoHelper.getTableInfo(t2.java)
        val sb = StringBuilder("`${table.tableName} ${tAlias}` ON ")
        val cmp = MPJCompare(this)
        onMS(cmp)
        if (cmp.conditions.isEmpty()) throw Exception("onMS至少一个条件")
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
            if(it.name in baseFieldMap){
                this.select(columnName(baseFieldMap[it.name]!!))
            }
        }
        return this
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
