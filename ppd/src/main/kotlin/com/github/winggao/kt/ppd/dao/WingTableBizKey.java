package com.github.winggao.kt.ppd.dao;

import java.lang.annotation.*;

/**
 * 除了id外的自定义业务主键
 * 一般配合 @TableField(updateStrategy = FieldStrategy.NEVER)
 * User: Wing
 * Date: 2021/5/14
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface WingTableBizKey {
}
