package com.github.winggao.kt.writer

import cn.hutool.core.date.DateUtil
import cn.hutool.core.text.csv.CsvUtil
import cn.hutool.core.util.CharsetUtil
import cn.hutool.core.util.ReflectUtil
import com.github.winggao.kt.filterMap
import io.swagger.annotations.ApiModelProperty
import java.io.File
import java.io.Writer
import java.lang.reflect.Field
import java.util.Date


/**
 * 将clz类型的数据写入文件
 */
open class RowWriter<T>(val clz: Class<T>) {
    // 日期
    var dateFormat = "yyyy-MM-dd HH:mm:ss"

    companion object {
        const val EXT_CSV = "csv"

        /**
         * 直接写入csv文件
         * @param filename 文件名，不含扩展名
         */
        fun writeCsv(filename: String, fetchRows: () -> Collection<Any>?): File {
            //预取
            val rows = fetchRows()!!
            val writer = RowWriter(rows.iterator().next().javaClass)
            var isFirst = true
            return writer.writeCsv(filename) {
                if (isFirst) {
                    isFirst = false
                    rows
                } else fetchRows()
            }
        }
    }

    private val myHeader by lazy { getHeader() }

    // prop映射到header
    open fun getHeader(): List<Pair<Field, String>> {
        return clz.declaredFields.filterMap({ !it.isSynthetic }, {
            val aa = it.getAnnotation(ApiModelProperty::class.java)
            it.isAccessible = true // 标记为可以获取
            if (aa != null) {
                it to aa.value
            } else {
                it to it.name
            }
        })
    }

    open fun fmtRow(row: T): Collection<Any?> {
        return myHeader.map {
            val v = it.first.get(row)
            if (v != null) {
                when (v) { //日期优化
                    is Date -> return@map DateUtil.format(v, dateFormat)
                }
            }
            v as Any?
        }
    }

    fun writeCsv(filename: String, fetchRows: () -> Collection<T>?): File {
        val tmp = File.createTempFile(filename, ".csv")
        val writer = CsvUtil.getWriter(tmp, CharsetUtil.CHARSET_UTF_8)
        //utf8-bom
        (ReflectUtil.getFieldValue(writer, "writer") as Writer).write("\ufeff")
        writer.write(myHeader.map { it.second }.toTypedArray())
        while (true) {
            val rows = fetchRows()
            if (rows == null) break //结束
            writer.write(rows.map { fmtRow(it) })
        }
        writer.close()
        return tmp
    }
}
