package com.github.winggao.kt

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

object ObjectUtils {
    fun <T : Any> toMap(obj: T): Map<String, Any?> {
        val out = HashMap<String, Any?>()
        (obj::class as KClass<T>).members.forEach { prop ->
            if (prop is KProperty) {
                out.put(prop.name, prop.call(obj))
            }
//            prop.name to prop.get(obj)?.let { value ->
//                if (value::class.isData) {
//                    toMap(value)
//                } else {
//                    value
//                }
//            }
        }
        return out
    }
}