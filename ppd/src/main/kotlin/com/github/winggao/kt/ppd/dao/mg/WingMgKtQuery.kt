package com.github.winggao.kt.ppd.dao.mg

import dev.morphia.query.Query
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

    fun <V> ge(column: KProperty<V>, obj: V): WingMgKtQuery<T> {
        raw.field(toColumnName(column)).greaterThanOrEq(obj)
        return this
    }

    fun toMg(): Query<T> {
        return raw
    }
}