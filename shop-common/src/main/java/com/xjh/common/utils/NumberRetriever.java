package com.xjh.common.utils;

public class NumberRetriever {
    String str;
    int pos = 0;

    NumberRetriever(String str) {
        this.str = str;
    }

    Integer next(int maxLen, Integer def) {
        if (str == null) {
            return def;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < maxLen && pos < str.length()) {
            char c = str.charAt(pos++);
            if (Character.isDigit(c)) {
                sb.append(c);
            } else if (sb.length() > 0) {
                break;
            }
        }
        if (sb.length() == 0) {
            return def;
        }
        return Integer.parseInt(sb.toString());
    }
}
