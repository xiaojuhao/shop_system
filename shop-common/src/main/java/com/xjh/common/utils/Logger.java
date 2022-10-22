package com.xjh.common.utils;

import com.xjh.common.store.SysConfigUtils;

import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class Logger {
    private static AtomicLong counter = new AtomicLong();
    private static AtomicBoolean exiting = new AtomicBoolean(false);

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

    public static void exiting(){
        exiting.set(true);
        doFlush(logWriter);
    }

    private static void append(String level, String msg) {
        try {
            if (logWriter == null) {
                return;
            }
            String time = DateBuilder.now().format("yyyy-MM-dd HH:mm:ss");
            StringBuffer log = new StringBuffer();
            log.append(time).append(" [").append(level).append("] ");
            if (CommonUtils.isNotBlank(CurrentRequest.requestId())) {
                log.append(CurrentRequest.requestId()).append(" >> ");
            }
            log.append(msg).append("\r\n");
            logWriter.append(log);
            counter.incrementAndGet();
            if(exiting.get()){
                doFlush(logWriter);
            }
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
