package com.xjh.common.anno;


import com.google.inject.BindingAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * table注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@BindingAnnotation
public @interface TableItemMark {
    String name();

    int order() default 0;

    int width();
}
