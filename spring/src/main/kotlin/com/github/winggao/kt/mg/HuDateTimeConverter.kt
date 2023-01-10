package com.github.winggao.kt.ppd.dao.mg

import cn.hutool.core.date.DateTime
import dev.morphia.converters.DateConverter
import dev.morphia.mapping.MappedField
import java.util.*


class HuDateTimeConverter : DateConverter(DateTime::class.java) {
    override fun decode(targetClass: Class<*>?, `val`: Any?, optionalExtraInfo: MappedField?): Any {
        val d = super.decode(targetClass, `val`, optionalExtraInfo) as Date
        return DateTime(d)
    }

    override fun encode(`val`: Any?, optionalExtraInfo: MappedField?): Any? {
        return if (`val` == null) {
            null
        } else (`val` as DateTime).toJdkDate()
    }
}