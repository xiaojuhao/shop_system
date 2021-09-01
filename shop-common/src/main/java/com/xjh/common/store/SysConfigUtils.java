package com.xjh.common.store;

import com.xjh.common.enumeration.EnumPropName;
import com.xjh.common.utils.Holder;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.TimeRecord;

import java.io.File;
import java.util.Properties;

public class SysConfigUtils {
    static String runtimePropertyFileName = "runtime.properties";
    static Holder<String> workDirHomeCache = new Holder<>();
    static Holder<Properties> propertiesHolder = new Holder<>();

    public static String getWorkDir() {
        if (workDirHomeCache.get() != null) {
            return workDirHomeCache.get();
        }
        TimeRecord time = TimeRecord.start();
        Properties prop = loadRuntimeProperties();
        String workDir = prop.getProperty(EnumPropName.WORK_DIR.name);
        Logger.info("获取WorkDir耗时 : " + time.getCost());
        if (workDir == null) {
            return null;
        }
        if (!workDir.endsWith("/")) {
            workDir = workDir + "/";
        }
        return workDirHomeCache.hold(workDir);
    }

    public static Properties getDbConfig() {
        return loadRuntimeProperties();
    }

    public static File userHomeDir() {
        String userHome = System.getProperty("user.home");
        File home = new File(userHome + "/ShopSystem/");
        if (!home.exists()) {
            if (!home.mkdirs()) {
                throw new RuntimeException("系统基础信息目录:" + home.getAbsolutePath());
            }
        }
        return home;
    }

    public static void dumpRuntimeProperties(Properties properties) {
        File file = new File(userHomeDir(), runtimePropertyFileName);
        PropertiesUtils.dumpProperties(file, properties);
        propertiesHolder.hold(null);
        workDirHomeCache.hold(null);
    }

    public static Properties loadRuntimeProperties() {
        if (propertiesHolder.get() != null) {
            return propertiesHolder.get();
        }
        File home = new File(userHomeDir(), runtimePropertyFileName);
        if (!home.exists()) {
            if (!home.mkdirs()) {
                throw new RuntimeException("系统基础信息目录:" + home.getAbsolutePath());
            }
        }
        Properties p = PropertiesUtils.loadProperties(home.getAbsolutePath(), runtimePropertyFileName);
        return propertiesHolder.hold(p);
    }
}
