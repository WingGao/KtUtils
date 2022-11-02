package com.github.winggao.kt

object StringUtilsW {
    /**
     * 判断String能否直接转换为内部对象
     */
    fun canConvert(clz: Class<*>): Boolean {
        return when (clz) {
            String::class.java, Int::class.java, Long::class.java, Float::class.java, Double::class.java -> true
            else -> false
        }
    }

    /**
     * 将String转换为内部对象
     */
    fun <T> convertTo(s: String?, clz: Class<T>): T? {
        if (s == null) return null
        return when (clz) {
            String::class.java -> s
            Int::class.java -> s.toIntOrNull()
            Long::class.java -> s.toLongOrNull()
            Float::class.java -> s.toFloatOrNull()
            Double::class.java -> s.toDoubleOrNull()
            else -> throw Exception("目前不支持 $clz")
        } as T?
    }
}