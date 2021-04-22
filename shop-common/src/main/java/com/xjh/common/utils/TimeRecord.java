package com.xjh.common.utils;

public class TimeRecord {
    long startTime = 0;

    public static TimeRecord start() {
        TimeRecord r = new TimeRecord();
        r.startTime = System.currentTimeMillis();
        return r;
    }

    public long getCostAndClear() {
        long cost = getCost();
        startTime = 0;
        return cost;
    }

    public long getCost() {
        return System.currentTimeMillis() - startTime;
    }

    public long getCostAndRestart() {
        long cost = getCost();
        startTime = 0;
        return cost;
    }

}
