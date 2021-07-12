package com.xjh.common.utils;

public class Holder<T> {
    T ref;

    public void set(T t) {
        this.ref = t;
    }

    public T get() {
        return ref;
    }

    public T hold(T t) {
        this.ref = t;
        return this.ref;
    }
}
