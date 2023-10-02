package com.xjh.common.utils;

import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static com.xjh.common.store.DirUtils.userHomeDir;
import static com.xjh.common.store.DirUtils.workDir;

public class Logger {
    private static final AtomicLong counter = new AtomicLong();
    private static final AtomicBoolean exiting = new AtomicBoolean(false);

    public static void info(String msg) {
        System.out.println("INFO:" + msg);
        append("INFO", msg);
    }

    public static void error(String msg) {
        System.err.println("ERROR:" + msg);
        append("ERROR", msg);
    }

    public static void logToHome(String msg) {
        if (homeLogger == null) {
            System.out.println("HomeWriter缺失，打印到标准输出");
            System.out.println(msg);
        }
        try {
            System.out.println(msg);
            homeLogger.append(msg).append("\n");
            homeLogger.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void exiting() {
        exiting.set(true);
        doFlush(workDirWriter);
    }

    private static void append(String level, String msg) {
        try {
            if (workDirWriter == null) {
                return;
            }
            String time = DateBuilder.now().format("yyyy-MM-dd HH:mm:ss");
            StringBuffer log = new StringBuffer();
            log.append(time).append(" [").append(level).append("] ");
            if (CommonUtils.isNotBlank(CurrentRequest.requestId())) {
                log.append(CurrentRequest.requestId()).append(" >> ");
            }
            log.append(msg).append("\r\n");
            workDirWriter.append(log);
            counter.incrementAndGet();
            if (exiting.get()) {
                doFlush(workDirWriter);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static FileWriter homeLogger = getHomerWriter();

    static FileWriter workDirWriter = getWorkDirWriter();


    private static FileWriter getWorkDirWriter() {
        try {
            String workDir = workDir();
            if (CommonUtils.isBlank(workDir)) {
                workDir = userHomeDir().getAbsolutePath();
            }
            String fileName = "shopsystem." + DateBuilder.today().format("yyyyMMdd") + ".log";
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

    private static FileWriter getHomerWriter() {
        try {
            String workDir = userHomeDir().getAbsolutePath();
            String fileName = "common." + DateBuilder.today().format("yyyyMMdd") + ".log";
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
        // 定期刷日志
        Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(() -> doFlush(fileWriter), 3, 3, TimeUnit.SECONDS);
    }

    private static void doFlush(FileWriter fileWriter) {
        try {
            if (counter.get() > 0) {
                counter.set(0);
                fileWriter.flush();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
