package com.xjh.common.utils;

public class ClickHelper {
    private static Holder<Long> lastClick = new Holder<>();

    public static boolean isDblClick() {
        if (lastClick.get() == null) {
            lastClick.hold(System.currentTimeMillis());
            return false;
        }
        long last = lastClick.get();
        if (System.currentTimeMillis() - last < 300) {
            lastClick.hold(null);
            return true;
        }
        lastClick.hold(System.currentTimeMillis());
        return false;
    }
}
