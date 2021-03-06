package com.xjh.common.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.xjh.common.store.SysConfigUtils;

public class Logger {
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
                    System.out.println("????????????????????????");
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
        // ???????????????
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
