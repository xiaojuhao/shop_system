package com.xjh.common.utils;

import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.xjh.common.store.SysConfigUtils;

public class Logger {
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
            if (logWriter == null) {
                return;
            }
            logWriter.append(DateBuilder.now().format("yyyy-MM-dd HH:mm:ss"));
            logWriter.append("[").append(level).append("]");
            if (CommonUtils.isNotBlank(CurrentRequest.requestId())) {
                logWriter.append(CurrentRequest.requestId()).append(" >> ");
            }
            logWriter.append(msg);
            logWriter.append("\r\n");
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
            String fileName = "common-" + DateBuilder.today().format("yyyy-MM-dd") + ".log";
            File logDir = new File(workDir + "/logs/");
            if (!logDir.exists()) {
                if (!logDir.mkdirs()) {
                    System.out.println("创建日志目录失败");
                }
            }
            FileWriter writer = new FileWriter(new File(logDir, fileName), true);
            flushInSchedule(writer);
            return writer;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static void flushInSchedule(FileWriter fileWriter) {
        // 退出时刷一次
        Runtime.getRuntime().addShutdownHook(new Thread(() -> doFlush(fileWriter)));
        // 定期刷日志
        Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(() -> doFlush(fileWriter), 3, 3, TimeUnit.SECONDS);
    }

    private static void doFlush(FileWriter fileWriter) {
        try {
            fileWriter.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
