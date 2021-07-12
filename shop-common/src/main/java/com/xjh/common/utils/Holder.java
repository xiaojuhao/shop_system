package com.xjh.common.utils;

public class Holder<T> {
    T ref;

    public T get() {
        return ref;
    }

    public T hold(T ref) {
        this.ref = ref;
        return this.ref;
    }
}
