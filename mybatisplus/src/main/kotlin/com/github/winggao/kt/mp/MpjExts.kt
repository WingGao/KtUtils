package com.github.winggao.kt.mp

import cn.hutool.core.util.ReflectUtil
import com.baomidou.mybatisplus.core.conditions.SharedString
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