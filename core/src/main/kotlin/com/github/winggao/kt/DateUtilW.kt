package com.github.winggao.kt

import cn.hutool.core.date.DateUtil
import java.util.*

object DateUtilW {
    /**
     * 到今天24点还有多少毫秒
     */
    fun tillEndOfDay(): Long {
        val t = Date()
        val ed = DateUtil.endOfDay(t)
        return ed.time - t.time
    }
}