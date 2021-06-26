package com.xjh.common.utils;

public class ClickHelper {
    private static Holder<Long> lastClick = new Holder<>();

    public static boolean isDblClick() {
        if (lastClick.get() == null) {
            lastClick.set(System.currentTimeMillis());
            return false;
        }
        long last = lastClick.get();
        if (System.currentTimeMillis() - last < 300) {
            lastClick.set(null);
            return true;
        }
        lastClick.set(System.currentTimeMillis());
        return false;
    }
}
