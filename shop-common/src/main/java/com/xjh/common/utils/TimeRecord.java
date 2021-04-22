package com.xjh.common.utils;

public class TimeRecord {
    long startTime = 0;

    public static TimeRecord start() {
        TimeRecord r = new TimeRecord();
        r.startTime = System.currentTimeMillis();
        return r;
    }

    public long getCost() {
        return System.currentTimeMillis() - startTime;
    }

    public long getCostAndReset() {
        long cost = getCost();
        startTime = System.currentTimeMillis();
        return cost;
    }

}
