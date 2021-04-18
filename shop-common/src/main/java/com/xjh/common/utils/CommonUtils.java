package com.xjh.common.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CommonUtils {
    public static <V, R> List<R> collect(List<V> list, Function<V, R> fun) {
        if (list == null || list.size() == 0) {
            return new ArrayList<>();
        }
        return list.stream().map(fun).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static void safeRun(Runnable run) {
        try {
            run.run();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static <T> T orElse(T v, T def) {
        if (v == null) {
            return v;
        } else {
            return def;
        }
    }

    public static boolean ne(Object a, Object b) {
        return !eq(a, b);
    }

    public static boolean eq(Object a, Object b) {
        if (a == null || b == null) {
            return false;
        }
        if (a instanceof LocalDateTime && b instanceof LocalDateTime) {
            return ((LocalDateTime) a).isEqual((LocalDateTime) b);
        }
        return a.equals(b);
    }

    public static String readAsString(InputStream stream) throws IOException {
        StringBuilder sb = new StringBuilder();
        int t;
        while ((t = stream.read()) != -1) {
            sb.append((char) t);
        }
        return sb.toString();
    }

    public static String readFile(String file) throws Exception {
        InputStreamReader ipr = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(ipr);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append(" ");
        }
        br.close();
        ipr.close();
        return sb.toString();
    }

    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }
}
