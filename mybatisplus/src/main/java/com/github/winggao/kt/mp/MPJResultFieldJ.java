package com.github.winggao.kt.mp;

import kotlin.reflect.*;
import kotlin.reflect.jvm.internal.KProperty1Impl;
import kotlin.reflect.jvm.internal.KPropertyImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;

@Target({java.lang.annotation.ElementType.FIELD, ElementType.TYPE, ElementType.TYPE_USE})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface MPJResultFieldJ {
    Class<?> table() default Object.class;

    String sourceColumn() default "";

    boolean ignore() default false;
}
