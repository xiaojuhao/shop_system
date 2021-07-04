package com.xjh.common.utils;

public class ThreadUtils {
    public static void runInNewThread(Runnable run) {
        Thread t = new Thread(run);
        t.setDaemon(true);
        t.start();
    }
}
