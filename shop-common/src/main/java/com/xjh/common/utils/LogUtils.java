package com.xjh.common.utils;

public class LogUtils {
    public static void info(String msg) {
        System.out.println("INFO:" + msg);
    }

    public static void trace(String msg) {
        System.out.println("TRACE:" + msg);
    }

    public static void warn(String msg) {
        System.err.println("WARN:" + msg);
    }

    public static void error(String msg) {
        System.err.println("ERROR:" + msg);
    }
}
