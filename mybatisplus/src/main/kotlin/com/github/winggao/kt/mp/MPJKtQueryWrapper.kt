package com.github.winggao.kt.mp

import com.baomidou.mybatisplus.core.enums.SqlKeyword
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper
import com.github.yulichang.query.MPJQueryWrapper
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.javaType
import kotlin.reflect.jvm.javaField

/***
 * 用于支持Kotlin的MPJQueryWrapper
 * 1. 支持Kotlin的Entity
 * 2. 支持TableLogic自动补全
 *
 * @param baseKC 底表的KClass
 */
@Deprecated("WIP")
class MPJKtQueryWrapper<T : Any>(baseKC: KClass<T>) : MPJQueryWrapper<T>() {
    private val tableAlias = HashMap<Class<*>, String>()
    private val baseKClass: KClass<T> = baseKC
    private val propCache = HashMap<KProperty<*>, KPropInfo>() //属性对应的表

    init {
        this.entityClass = baseKC.java
        addAlias(baseKC, "t")
        val table = TableInfoHelper.getTableInfo(this.entityClass)
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

    fun columnName(
        p: KProperty1<*, *>,
        withTable: Boolean = true,
        colSubScope: String? = null,
        checkIgnore: Boolean = false
    ): String {
        val propInfo = propCache[p]
        if (propInfo == null) {
            if (checkIgnore) return ""
            return p.name
        }
        // MBP的分页插件无法区分where条件,所以需要手动加`it.setOptimizeJoinOfCountSql(false)`
        var col = if (withTable) {
            "${tableAlias[propInfo.parent.java]}.${propInfo.column}" //取别名
        } else "${propInfo.column}"
        if (colSubScope != null) return "$col AS `$colSubScope.${propInfo.column}`"
        return col
    }

    fun valueToStr(v: Any): String {
        return when (v) {
            is KProperty1<*, *> -> columnName(v)
            is Collection<*> -> inExpression(v).sqlSegment
            else -> formatParam(null, v)
        }
    }

    fun <E : Any> leftJoin(t2: KClass<E>, onMS: (MPJCompare) -> Unit, alias: String? = null): MPJKtQueryWrapper<T> {
        //设置别名
        var tAlias = alias
        if (tAlias.isNullOrEmpty()) tAlias = "t${tableAlias.size + 1}"
        addAlias(t2, tAlias)
        //获取表原名
        val table = TableInfoHelper.getTableInfo(t2.java)
        val sb = StringBuilder("${table.tableName} ${tAlias} ON ")
        val cmp = MPJCompare(this)
        onMS(cmp)
        if (cmp.conditions.isEmpty()) throw Exception("onMS至少一个条件")
        //补全TableLogic
        if (table.isWithLogicDelete) sb.append("${tAlias}.${table.logicDeleteFieldInfo.column}=${table.logicDeleteFieldInfo.logicNotDeleteValue} AND ")
        cmp.conditions.forEachIndexed { index, c ->
            if (index > 0) sb.append(" AND ")
            sb.append("${columnName(c.column)} ${c.op.sqlSegment} ${valueToStr(c.value)}")
        }
        this.leftJoin(sb.toString())
        return this
    }

    fun where(block: (MPJCompare) -> Unit): MPJKtQueryWrapper<T> {
        val cmp = MPJCompare(this)
        block(cmp)
        cmp.conditions.forEach { c ->
            this.appendSqlSegments(columnToSqlSegment(columnName(c.column)), c.op, { valueToStr(c.value) })
//            this.addCondition(true, columnName(c.column), c.op, valueToStr(c.value))
        }
        return this
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun ktSelect(entity: KClass<*>): MPJKtQueryWrapper<T> {
        val baseFieldMap = this.baseKClass.memberProperties.map { it.name to it }.toMap()
        entity.memberProperties.forEach {
            //先判断在不在join表里
            val jClz = it.returnType.javaType
            if (jClz == null) return@forEach
            val mKC = it.returnType.classifier as KClass<*>
            val alias = tableAlias[jClz]
            if (alias != null) { //整个prop都是这个表
                ktSelect(mKC.memberProperties, subScope = it.name)
                return@forEach
            }
            val mpjr = it.javaField!!.getAnnotation(MPJResultFieldJ::class.java)
            if (mpjr != null && !mpjr.ignore) {
                if (mpjr.table.java != Object::class.java) {
                    val targetTableKC = mpjr.table
                    ktSelect(mKC.memberProperties, targetTable = targetTableKC, subScope = it.name)
                }
            }
            //匹配底表
            if (it.name in baseFieldMap) {
                ktSelect(baseFieldMap[it.name]!!, "", checkExist = true)
            }
        }
        return this
    }

    fun ktSelect(
        props: Collection<KProperty1<*, *>>,
        targetTable: KClass<*>? = null,
        subScope: String? = null
    ): MPJKtQueryWrapper<T> {
        var propMap = HashMap<String, KPropInfo>()
        if (targetTable != null) { //映射到员表的column
            propMap = HashMap(targetTable.memberProperties.map { it.name to propCache[it]!! }.toMap())
        }
        props.forEach {
            val tProp = propMap[it.name]?.kProp ?: it
            this.select(columnName(tProp as KProperty1<*, *>, colSubScope = subScope))
        }
        return this
    }

    /**
     * 核心筛选
     * @param 数据导出到prop
     * @param srcColumn 源数据库的column
     * @param checkExist 是否检查TableField
     */
    fun ktSelect(prop: KProperty1<*, *>, srcColumn: String, checkExist: Boolean = false): MPJKtQueryWrapper<T> {
        if (checkExist) {
            val propInfo = propCache[prop] ?: return this
        }
        if (srcColumn.isNullOrEmpty()) this.select(columnName(prop))
        else this.select("$srcColumn AS ${columnName(prop)}")
        return this
    }

    fun ktSelect(prop: KProperty1<*, *>, srcColumnProp: KProperty1<*, *>): MPJKtQueryWrapper<T> {
        this.select("${columnName(srcColumnProp)} AS ${columnName(prop)}")
        return this
    }

    fun groupBy(vararg props: KProperty1<*, *>): MPJKtQueryWrapper<T> {
        props.forEach {
            this.groupBy(columnName(it))
        }
        return this
    }

    fun orderByDesc(vararg props: KProperty1<*, *>): MPJKtQueryWrapper<T> {
        this.orderByDesc(props.map { columnName(it) })
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

    fun ge(column: KProperty1<*, *>, value: Any): MPJCompare {
        conditions.add(CompareItem(SqlKeyword.GE, column, value))
        return this
    }

    fun `in`(column: KProperty1<*, *>, value: Any): MPJCompare {
        conditions.add(CompareItem(SqlKeyword.IN, column, value))
        return this
    }

    class CompareItem<T, V>(val op: SqlKeyword, val column: KProperty1<T, V>, val value: Any) {
        fun toSql(wrapper: MPJKtQueryWrapper<*>): String {
            return "${wrapper.columnName(column)} ${op.sqlSegment} ${wrapper.valueToStr(value)}"
        }
    }
}
