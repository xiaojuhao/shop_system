package com.xjh.dao.foundation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@BindingAnnotation
public @interface Table {
    String value() default "";
}
