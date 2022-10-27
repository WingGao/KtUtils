package com.github.winggao.kt.ppd.dao.mg

import com.github.winggao.kt.request.WPage
import com.github.winggao.kt.request.WPageT
import dev.morphia.query.FindOptions
import dev.morphia.query.Query
import kotlin.math.max
import kotlin.reflect.KProperty

class WingMgKtQuery<T : MongoEntity> {
    lateinit var raw: Query<T>
    lateinit var service: WingMgService<T>

    constructor(service: WingMgService<T>) {
        this.service = service
        raw = service.query()
    }

    private fun toColumnName(p: KProperty<*>): String {
//        val k = p
        return service.columnMap[p.name]!!
    }

    fun `in`(column: KProperty<*>, values: Collection<*>): WingMgKtQuery<T> {
        raw.field(toColumnName(column)).`in`(values)
        return this
    }

    fun isNotNull(column: KProperty<*>): WingMgKtQuery<T> {
        raw.field(toColumnName(column)).also {
            it.exists()
            it.notEqual(null)
        }
        return this
    }

    fun <V> eq(column: KProperty<V>, obj: V): WingMgKtQuery<T> {
        raw.field(toColumnName(column)).equal(obj)
        return this
    }

    fun <V> ne(column: KProperty<V>, obj: V): WingMgKtQuery<T> {
        raw.field(toColumnName(column)).notEqual(obj)
        return this
    }

    fun <V> gt(column: KProperty<V>, obj: V): WingMgKtQuery<T> {
        raw.field(toColumnName(column)).greaterThan(obj)
        return this
    }

    fun <V> ge(column: KProperty<V>, obj: V): WingMgKtQuery<T> {
        raw.field(toColumnName(column)).greaterThanOrEq(obj)
        return this
    }

    fun <V> lt(column: KProperty<V>, obj: V): WingMgKtQuery<T> {
        raw.field(toColumnName(column)).lessThan(obj)
        return this
    }

    fun <V> le(column: KProperty<V>, obj: V): WingMgKtQuery<T> {
        raw.field(toColumnName(column)).lessThanOrEq(obj)
        return this
    }

    fun orderBy(column: KProperty<*>): WingMgKtQuery<T> {
        raw.order(toColumnName(column))
        return this
    }

    fun orderByDesc(column: KProperty<*>): WingMgKtQuery<T> {
        raw.order("-" + toColumnName(column))
        return this
    }

    fun page(p: WPage): WPageT<T> {
        val count = raw.count()
        val psize = p.size?.toInt() ?: 10
        val outPage = WPageT<T>().also {
            it.current = max(1L, p.current)
            it.size = psize.toLong()
            it.total = count
        }
        val skip = (outPage.current.toInt() - 1) * psize
        if (skip >= count) return outPage

        outPage.records = raw.find(
            FindOptions().skip(skip)
                .limit(psize)
        ).toList()
        return outPage
    }

    fun toMg(): Query<T> {
        return raw
    }
}