package com.github.winggao.kt

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WingEnumTest {
    @Test
    fun testHelper() {
        assertEquals(TestEnumA.A, TestEnumA.helper.keyOf("A"))
        assertEquals(null, TestEnumA.helper.keyOf("AA"))
    }
}