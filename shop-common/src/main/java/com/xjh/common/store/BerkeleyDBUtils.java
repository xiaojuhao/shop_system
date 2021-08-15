package com.xjh.common.store;

import java.io.File;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.xjh.common.utils.Logger;

public class BerkeleyDBUtils {
    public static Environment getEnv() {
        String workDir = SysConfigUtils.getWorkDir();
        File homeDirectory = new File(workDir, "database/bdb");
        Logger.info("home path : " + homeDirectory.getAbsolutePath());
        if (!homeDirectory.exists()) {
            homeDirectory.mkdirs();
        }
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setTransactional(true);
        envConfig.setAllowCreate(true);
        // env
        return new Environment(homeDirectory, envConfig);
    }

}
