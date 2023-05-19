package com.github.winggao.kt.reflect

import cn.hutool.core.util.ReflectUtil
import kotlin.jvm.internal.CallableReference
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

object ReflectExt {
    fun <T : Any, V> getOwnerClass(p: KProperty1<T, V>): KClass<T> {
        val kc: KCallable<*>
        if (p is CallableReference) {
            kc = p.compute()
        } else if (p is KCallable<*>) {
            kc = p
        } else {
            throw Exception("not support")
        }
        return ReflectUtil.getFieldValue(kc, "container") as KClass<T>
    }
}