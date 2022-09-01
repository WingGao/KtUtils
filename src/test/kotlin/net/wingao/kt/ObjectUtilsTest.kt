package net.wingao.kt

import com.alibaba.fastjson.JSON
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

class ObjectUtilsTest {
    @Test
    fun testToMap() {
        val a = TestClassA().also {
            it.mString = "a"
            it.mInt = 11
            it.mBoolean = true
            it.mDate = Date()
        }
        val m = ObjectUtils.toMap(a)
        assertEquals(a.mInt,m["mInt"])
        println(JSON.toJSONString(m))
    }
}