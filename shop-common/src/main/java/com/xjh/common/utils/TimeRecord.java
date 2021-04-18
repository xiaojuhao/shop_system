package com.xjh.common.utils;

public class TimeRecord {
    static ThreadLocal<Long> startTime = new ThreadLocal<>();

    public static void clear() {
        startTime.remove();
    }

    public static void start() {
        clear();
        startTime.set(System.currentTimeMillis());
    }

    public static long getCostAndClear() {
        long cost = getCost();
        clear();
        return cost;
    }

    public static long getCost() {
        if (startTime.get() == null) {
            start();
            return 0;
        }
        return System.currentTimeMillis() - startTime.get();
    }

    public static long getCostAndRestart() {
        long cost = getCost();
        start();
        return cost;
    }

}
