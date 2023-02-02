package com.github.winggao.kt.mp

/**
 * 除了id外的自定义业务主键
 * 一般配合 @TableField(updateStrategy = FieldStrategy.NEVER)
 * User: Wing
 * Date: 2021/5/14
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD, AnnotationTarget.ANNOTATION_CLASS)
annotation class WingTableBizKey
