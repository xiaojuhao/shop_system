package com.xjh.common.utils;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayedRunnable implements Delayed {
    public Runnable runnable;
    public long startMills;

    public DelayedRunnable(Runnable runnable, long delayedSec) {
        this.runnable = runnable;
        this.startMills = delayedSec * 1000 + System.currentTimeMillis();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = startMills - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        if (!(o instanceof DelayedRunnable)) {
            return 0;
        }
        DelayedRunnable dr = (DelayedRunnable) o;
        return Long.compare(this.startMills, dr.startMills);
    }
}
