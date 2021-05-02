package com.xjh.common.store;

import org.rocksdb.Options;
import org.rocksdb.RocksDBException;
import org.rocksdb.TtlDB;

import java.io.File;

public class RocksDbUtilization {
    public static TtlDB getDB(String dbname) throws RocksDBException {
        File home = new File(".rundata/kv/rocksdb/" + dbname);
        System.out.println("根目录:" + home.getAbsolutePath());
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
