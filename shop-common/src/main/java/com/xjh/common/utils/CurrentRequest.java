package com.xjh.common.utils;

public class CurrentRequest {
    private static ThreadLocal<String> requestId = new ThreadLocal<>();

    public static Runnable resetRequestId() {
        if (CommonUtils.isBlank(requestId.get())) {
            requestId.set(CommonUtils.randomStr(10));
            return CurrentRequest::clear;
        } else {
            return () -> {
            };
        }
    }

    public static String requestId() {
        return requestId.get();
    }

    public static void clear() {
        requestId.remove();
    }
}
