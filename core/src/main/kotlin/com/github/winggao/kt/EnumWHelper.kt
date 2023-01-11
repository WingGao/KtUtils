package com.github.winggao.kt

class EnumWHelper<T : Enum<T>, K>(private val eClz: Class<T>, val getKey: (T) -> K) {
    private val entityMap = eClz.enumConstants.map { getKey(it) to it }.toMap()

    fun keyOf(k: K): T? {
        return entityMap[k]
    }
}