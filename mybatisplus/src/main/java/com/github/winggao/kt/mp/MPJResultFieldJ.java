package com.github.winggao.kt.mp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.FIELD, ElementType.TYPE,ElementType.TYPE_USE})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface MPJResultFieldJ {
    Class<?> table() default Object.class;
}
