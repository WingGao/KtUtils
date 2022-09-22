package com.github.winggao.kt

import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
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

    /**
     * 将指定fields从src拷贝至dest
     */
    fun <S> copyFields(src: S, dest: S, fields: Collection<KMutableProperty1<S, *>>): S {
        fields.forEach { f ->
            f as KMutableProperty1<S, Any?>
            val v = f.get(src)
            f.set(dest, v)
        }
        return dest
    }

    /**
     * 有条件的将指定fields从src拷贝至dest
     */
    fun <S> copyFieldsFilter(src: S, dest: S, fields: Map<KMutableProperty1<S, *>, (srcV: Any?, desV: Any?) -> Boolean>): S {
        fields.forEach { f, check ->
            f as KMutableProperty1<S, Any?>
            val v = f.get(src)
            val dv = f.get(dest)
            if (check(v, dv)) f.set(dest, v)
        }
        return dest
    }
}