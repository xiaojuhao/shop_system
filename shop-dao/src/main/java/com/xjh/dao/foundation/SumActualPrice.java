package com.xjh.dao.foundation;

import com.google.inject.BindingAnnotation;
import com.xjh.common.enumeration.EnumPayMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@BindingAnnotation
public @interface SumActualPrice {
    EnumPayMethod value();
}
