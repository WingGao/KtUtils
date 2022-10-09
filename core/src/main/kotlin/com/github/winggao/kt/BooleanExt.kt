package com.github.winggao.kt

fun Boolean?.isTrue(): Boolean {
    return this != null && this
}

fun Boolean.doNotNull(yesAct: () -> Unit, noAct: () -> Unit): Boolean {
    when (this) {
        true -> yesAct()
        false -> noAct()
    }
    return this
}

fun Boolean?.doNotNull(yesAct: () -> Unit, noAct: () -> Unit): Boolean? {
    if (this == null) return null
    return this!!.doNotNull(yesAct, noAct)
}
