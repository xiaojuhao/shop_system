package com.xjh.common.store;

import com.xjh.common.utils.Holder;
import com.xjh.common.utils.LogUtils;
import com.xjh.common.utils.TimeRecord;

import java.io.File;
import java.util.Properties;

public class SysConfigUtils {

    static Holder<String> cache = new Holder<>();

    public static String getWorkDir() {
        if (cache.get() != null) {
            return cache.get();
        }
        TimeRecord time = TimeRecord.start();
        Properties prop = loadRuntimeProperties();
        String workDir = prop.getProperty("workDir");
        LogUtils.info("获取WorkDir耗时 : " + time.getCost());
        if (workDir == null) {
            return null;
        }
        if (!workDir.endsWith("/")) {
            workDir = workDir + "/";
        }
        cache.set(workDir);
        return workDir;
    }

    public static void setWorkDir(String dir) {
        Properties prop = loadRuntimeProperties();
        prop.put("workDir", dir);
        dumpRuntimeProperties(prop);
        cache.set(null);
    }

    private static void dumpRuntimeProperties(Properties properties) {
        String userHome = System.getProperty("user.home");
        File home = new File(userHome + "/ShopSystem/");
        if (!home.exists()) {
            if (!home.mkdirs()) {
                throw new RuntimeException("系统基础信息目录:" + home.getAbsolutePath());
            }
        }
        // LogUtils.info("回写配置系统目录:" + home.getAbsolutePath());
        File file = new File(home.getAbsolutePath() , "runtime.properties");
        PropertiesUtils.dumpProperties(file, properties);
    }

    private static Properties loadRuntimeProperties() {
        String userHome = System.getProperty("user.home");
        File home = new File(userHome + "/ShopSystem/");
        if (!home.exists()) {
            if (!home.mkdirs()) {
                throw new RuntimeException("系统基础信息目录:" + home.getAbsolutePath());
            }
        }
        // LogUtils.info("系统目录:" + home.getAbsolutePath());
        return PropertiesUtils.loadProperties(home.getAbsolutePath(), "runtime.properties");
    }

//    private static String get(String key) {
//        Runnable close = CommonUtils::emptyAction;
//        try {
//            TtlDB db = openDB();
//            close = () -> CommonUtils.safeRun(db::close);
//            byte[] imgPathB = db.get(key.getBytes());
//            if (imgPathB != null) {
//                return new String(imgPathB);
//            }
//            return null;
//        } catch (Exception ex) {
//            AlertBuilder.ERROR("系统异常", ex.getMessage()).showAndWait();
//        } finally {
//            close.run();
//        }
//        return null;
//    }
//
//    private static void set(String key, String val) {
//        if (val == null) {
//            return;
//        }
//        Runnable close = CommonUtils::emptyAction;
//        try {
//            TtlDB db = openDB();
//            close = () -> CommonUtils.safeRun(db::close);
//            db.put(key.getBytes(), val.getBytes());
//        } catch (Exception ex) {
//            AlertBuilder.ERROR("系统异常", ex.getMessage()).showAndWait();
//        } finally {
//            close.run();
//        }
//    }
//
//    private static TtlDB openDB() throws RocksDBException {
//        String userHome = System.getProperty("user.home");
//
//        File home = new File(userHome + "/ShopSystem/.config");
//        // LogUtils.info("系统基础信息目录:" + home.getAbsolutePath());
//        if (!home.exists()) {
//            if (!home.mkdirs()) {
//                throw new RocksDBException("系统基础信息目录:" + home.getAbsolutePath());
//            }
//        }
//        TtlDB.loadLibrary();
//        final Options options = new Options();
//        options.setCreateIfMissing(true);
//
//        return TtlDB.open(options, home.getAbsolutePath());
//    }
}
