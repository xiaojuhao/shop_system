package com.xjh.common.store;

import java.io.File;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.xjh.common.utils.LogUtils;

public class BerkeleyDBEnv {
    static Environment staticEnv = null;

    public static Environment getEnv() {
        if (staticEnv != null) {
            return staticEnv;
        }
        synchronized (BerkeleyDBEnv.class) {
            if (staticEnv != null) {
                return staticEnv;
            }
            String workDir = SysConfigUtils.getWorkDir();
            File homeDirectory = new File(workDir, "rundata/bdb");
            LogUtils.info("home path : " + homeDirectory.getAbsolutePath());
            if (!homeDirectory.exists()) {
                homeDirectory.mkdirs();
            }
            EnvironmentConfig envConfig = new EnvironmentConfig();
            envConfig.setTransactional(true);
            envConfig.setAllowCreate(true);
            // env
            Environment env = new Environment(homeDirectory, envConfig);
            staticEnv = env;
            return env;
        }
    }

}
