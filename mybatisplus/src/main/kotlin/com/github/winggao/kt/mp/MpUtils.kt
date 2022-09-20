package com.github.winggao.kt.mp

import cn.hutool.core.util.ReflectUtil
import com.baomidou.mybatisplus.core.conditions.SharedString
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils
import com.baomidou.mybatisplus.core.toolkit.StringPool
import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper

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