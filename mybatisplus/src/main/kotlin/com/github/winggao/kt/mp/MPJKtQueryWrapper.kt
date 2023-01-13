package com.github.winggao.kt.mp

import com.baomidou.mybatisplus.core.enums.SqlKeyword
import com.baomidou.mybatisplus.extension.kotlin.KtQueryChainWrapper
import com.github.yulichang.query.MPJQueryWrapper
import com.github.yulichang.wrapper.MPJLambdaWrapper
import kotlin.reflect.KProperty

//TODO
class MPJKtQueryWrapper<T> : MPJLambdaWrapper<T>() {
    private val tableAlias = HashMap<Class<*>, String>()

    /**
     * 设置别名
     */
    fun setAlias(entity: Class<*>, t: String): MPJKtQueryWrapper<T> {
        tableAlias[entity] = t
        return this
    }

    fun <E> leftJoin(t2: Class<E>, alias: String? = null, onMS: (MPJCompare) -> Unit) {

    }
}

class MPJCompare(val wrapper: MPJKtQueryWrapper<*>) {
    fun eq(column: KProperty<*>, value: Any): MPJCompare {
        return addCondition(column, SqlKeyword.EQ, value)
    }

    fun addCondition(column: KProperty<*>, sqlKeyword: SqlKeyword, v: Any): MPJCompare {
//        wrapper.eq()
        return this
    }
}
