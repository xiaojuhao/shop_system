package com.xjh.common.utils;

public class OrElse {
    public static <T> T orGet(T v, T def) {
        if (v instanceof String && ((String) v).trim().length() == 0) {
            return def;
        }
        return v != null ? v : def;
    }
}
