package com.xjh.common.utils;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class DelayHelper {
    static DelayQueue<DelayedRunnable> delayQueue = new DelayQueue<>();

    static AtomicBoolean started = new AtomicBoolean(false);

    static ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void delayRun(Runnable run, int delaySec) {
        delayQueue.offer(new DelayedRunnable(run, delaySec));
        trigger();
    }

    public static void trigger() {
        if (started.compareAndSet(false, true)) {
            executorService.submit(() -> {
                while (true) {
                    DelayedRunnable dr = delayQueue.take();
                    Safe.run(dr.runnable);
                }
            });
        }
    }

}
