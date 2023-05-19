package com.github.winggao.kt


fun Int?.isNullOrZero(): Boolean {
    return this == null || this == 0
}

fun Long?.isNullOrZero(): Boolean {
    return this == null || this == 0L
}

// 正数>0
fun Int?.isPositive(): Boolean {
    return this != null && this > 0
}

fun Long?.isPositive(): Boolean {
    return this != null && this > 0
}

