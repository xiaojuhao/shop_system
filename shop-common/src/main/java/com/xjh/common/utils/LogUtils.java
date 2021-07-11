package com.xjh.common.utils;

import java.io.File;
import java.io.FileWriter;

import com.xjh.common.store.SysConfigUtils;

public class LogUtils {
    public static void info(String msg) {
        System.out.println("INFO:" + msg);
        append("INFO", msg);
    }

    public static void trace(String msg) {
        System.out.println("TRACE:" + msg);
        append("TRACE", msg);
    }

    public static void warn(String msg) {
        System.err.println("WARN:" + msg);
        append("WARN", msg);
    }

    public static void error(String msg) {
        System.err.println("ERROR:" + msg);
        append("ERROR", msg);
    }

    private static void append(String level, String msg) {
        try {
            logWriter.append(DateBuilder.now().format("yyyy-MM-dd HH:mm:ss"));
            logWriter.append("[").append(level).append("]");
            logWriter.append(msg);
            logWriter.append("\r\n");
            logWriter.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static FileWriter logWriter = getWriter();

    private static FileWriter getWriter() {
        try {
            String workDir = SysConfigUtils.getWorkDir();
            if (CommonUtils.isBlank(workDir)) {
                workDir = SysConfigUtils.userHomeDir().getAbsolutePath();
            }
            File commonLog = new File(workDir, "common.log");
            return new FileWriter(commonLog, true);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
