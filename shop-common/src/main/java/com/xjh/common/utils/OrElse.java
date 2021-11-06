package com.xjh.common.utils;

public class OrElse {
    public static <T> T orGet(T v, T def) {
        return v != null ? v : def;
    }
}
