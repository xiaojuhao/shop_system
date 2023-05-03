package com.xjh.common.store;

import com.xjh.common.utils.Holder;

import java.io.File;
import java.util.Properties;

import static com.xjh.common.store.DirUtils.userHomeDir;

public class SysConfigUtils {
    static String runtimePropertyFileName = "runtime.properties";

    static Holder<Properties> propertiesHolder = new Holder<>();


    public static Properties getDbConfig() {
        return loadRuntimeProperties();
    }

    public static void dumpRuntimeProperties(Properties properties) {
        File file = new File(userHomeDir(), runtimePropertyFileName);
        PropertiesUtils.dumpProperties(file, properties);
        propertiesHolder.hold(null);
    }

    public static Properties loadRuntimeProperties() {
        if (propertiesHolder.get() != null) {
            return propertiesHolder.get();
        }
        File home = userHomeDir();
        if (!home.exists()) {
            if (!home.mkdirs()) {
                throw new RuntimeException("系统基础信息目录:" + home.getAbsolutePath());
            }
        }
        Properties p = PropertiesUtils.loadProperties(home.getAbsolutePath(), runtimePropertyFileName);
        return propertiesHolder.hold(p);
    }
}
