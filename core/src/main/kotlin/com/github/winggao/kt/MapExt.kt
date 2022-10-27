package com.github.winggao.kt

fun <K, V, R> Map<K, V>.chunked(size: Int, transform: (Map<K, V>) -> R) {
    this.keys.chunked(size) { keys ->
        val sMap = HashMap<K, V>()
        keys.forEach {
            sMap.put(it, this.get(it)!!)
        }
        transform(sMap)
    }
}