package com.github.winggao.kt.mp

import cn.hutool.core.util.ReflectUtil
import com.baomidou.mybatisplus.core.conditions.SharedString
import com.github.yulichang.query.MPJQueryWrapper

/**
 * 清除select信息
 */
fun <T> MPJQueryWrapper<T>.clearSelect() {
    (ReflectUtil.getFieldValue(this, "selectColumns") as MutableList<String>).clear()
    (ReflectUtil.getFieldValue(this, "sqlSelect") as SharedString).toNull()
}
