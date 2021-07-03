package com.xjh.common.store;

import java.io.File;

import org.rocksdb.Options;
import org.rocksdb.RocksDBException;
import org.rocksdb.TtlDB;

import com.xjh.common.utils.LogUtils;

public class RocksDbUtilization {
    public static TtlDB getDB(String dbname) throws RocksDBException {
        String workDir = SysConfigUtils.getWorkDir();
        File home = new File(workDir + "database/kv/rocksdb/" + dbname);
        LogUtils.info("根目录:" + home.getAbsolutePath());
        if (!home.exists()) {
            if (!home.mkdirs()) {
                throw new RocksDBException("创建数据库失败:" + home.getAbsolutePath());
            }
        }
        TtlDB.loadLibrary();
        final Options options = new Options();
        options.setCreateIfMissing(true);

        return TtlDB.open(options, home.getAbsolutePath());
    }
}
