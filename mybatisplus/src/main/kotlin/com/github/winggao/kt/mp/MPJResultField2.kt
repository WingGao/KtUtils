package com.github.winggao.kt.mp

import kotlin.reflect.KClass

/**
 * 结果的注释
 * @param table 结果对应的表
 */
@Deprecated("有BUG")
@Target(
    AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.CLASS,
    AnnotationTarget.TYPE
)
annotation class MPJResultField(val table: KClass<*>)


