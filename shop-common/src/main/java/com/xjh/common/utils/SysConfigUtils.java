package com.xjh.common.utils;

import java.io.File;

import org.rocksdb.Options;
import org.rocksdb.RocksDBException;
import org.rocksdb.TtlDB;

public class SysConfigUtils {

    static Holder<String> cache = new Holder<>();

    public static String getWorkDir() {
        if (cache.get() != null) {
            return cache.get();
        }
        TimeRecord time = TimeRecord.start();
        String workDir = get("workDir");
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
        set("workDir", dir);
        cache.set(null);
    }

    private static String get(String key) {
        Runnable close = CommonUtils::emptyAction;
        try {
            TtlDB db = openDB();
            close = () -> CommonUtils.safeRun(db::close);
            byte[] imgPathB = db.get(key.getBytes());
            if (imgPathB != null) {
                return new String(imgPathB);
            }
            return null;
        } catch (Exception ex) {
            AlertBuilder.ERROR("系统异常", ex.getMessage()).showAndWait();
        } finally {
            close.run();
        }
        return null;
    }

    private static void set(String key, String val) {
        if (val == null) {
            return;
        }
        Runnable close = CommonUtils::emptyAction;
        try {
            TtlDB db = openDB();
            close = () -> CommonUtils.safeRun(db::close);
            db.put(key.getBytes(), val.getBytes());
        } catch (Exception ex) {
            AlertBuilder.ERROR("系统异常", ex.getMessage()).showAndWait();
        } finally {
            close.run();
        }
    }

    private static TtlDB openDB() throws RocksDBException {
        String userHome = System.getProperty("user.home");

        File home = new File(userHome + "/ShopSystem/.config");
        // LogUtils.info("系统基础信息目录:" + home.getAbsolutePath());
        if (!home.exists()) {
            if (!home.mkdirs()) {
                throw new RocksDBException("系统基础信息目录:" + home.getAbsolutePath());
            }
        }
        TtlDB.loadLibrary();
        final Options options = new Options();
        options.setCreateIfMissing(true);

        return TtlDB.open(options, home.getAbsolutePath());
    }
}
