package com.github.winggao.kt

import com.alibaba.fastjson.JSON

object JsonUtilsW {
    fun <T : Any> parse(s: String, outClz: Class<T>): T? {
        val (res, ok) = StringUtilsW.tryConvertTo(s, outClz)
        if (ok) return res
        return JSON.parseObject(s, outClz)
    }
}