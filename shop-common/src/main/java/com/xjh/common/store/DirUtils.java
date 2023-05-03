package com.xjh.common.store;

import com.xjh.common.utils.Holder;
import com.xjh.common.utils.Logger;

import java.io.File;

public class DirUtils {
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

    static Holder<String> workdirHolder = new Holder<>();

    public static String workDir() {
        if (workdirHolder.get() != null) {
            return workdirHolder.get();
        }
        String path = DirUtils.class.getProtectionDomain().getCodeSource().getLocation().getFile();
        File file = new File(path);
        if (!file.isDirectory()) {
            path = file.getParent();
        }
        path = path.replace("target/classes/", "");
        path = path.replace("target\\classes\\", "");
        path = path.replace("shop-common/", "");
        path = path.replace("shop-common\\", "");
        Logger.logToHome("工作目录: " + path);
        workdirHolder.hold(path);
        return path;
    }
}
