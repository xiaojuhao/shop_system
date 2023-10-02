package com.xjh.common.store;

import com.xjh.common.utils.Holder;
import com.xjh.common.utils.Logger;

import java.io.File;

public class DirUtils {
    public static File userHomeDir() {
        String userHome = System.getProperty("user.home");
        try {
            userHome = java.net.URLDecoder.decode(userHome, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
        String path = DirUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            path = java.net.URLDecoder.decode(path, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        File file = new File(path);
        if (!file.isDirectory()) {
            path = file.getParent();
        }
        path = path.replace("target/classes/", "");
        path = path.replace("target\\classes\\", "");
        path = path.replace("shop-common/", "");
        path = path.replace("shop-common\\", "");
        Logger.logToHome("DirUtil.workDir() >> " + path);
        workdirHolder.hold(path);
        return path;
    }
}
