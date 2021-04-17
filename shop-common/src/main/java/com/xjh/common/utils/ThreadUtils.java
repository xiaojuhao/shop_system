package com.xjh.common.utils;

public class ThreadUtils {
    public static void runInDaemon(Runnable run) {
        Thread t = new Thread(run);
        t.setDaemon(true);
        t.start();
    }
}
