package com.github.winggao.kt

import java.util.Date

class TestClassA {
    var mString: String? = null
    var mInt: Int? = null
    var mBoolean: Boolean? = null
    var mDate: Date? = null
}

enum class TestEnumA(val key: String, val desc: String) {
    A("A", "DESC_A"),
    B("B", "DESC_B"),
    C("C", "DESC_C");

    companion object {
        val helper = EnumWHelper(TestEnumA::class.java, { it.key })
    }
}