package com.github.winggao.kt

object CollectionUtilsW {

}

/**
 * 支持有条件的转换为map
 */
fun <T, K, V> kotlin.collections.Iterable<T>.filterHashMap(predicate: (T) -> Triple<Boolean, K, V>): HashMap<K, V> {
    val out = HashMap<K, V>()
    this.forEach {
        val (t, k, v) = predicate(it)
        if (t) out[k] = v
    }
    return out
}

fun <T, R> kotlin.collections.Iterable<T>.filterMap(predicate: (T) -> Boolean, trans: (T) -> R): ArrayList<R> {
    val out = ArrayList<R>()
    forEach {
        if (predicate(it)) out.add(trans(it))
    }
    return out
}

fun <T, R> Array<out T>.filterMap(predicate: (T) -> Boolean, trans: (T) -> R): ArrayList<R> {
    val out = ArrayList<R>()
    forEach {
        if (predicate(it)) out.add(trans(it))
    }
    return out
}
