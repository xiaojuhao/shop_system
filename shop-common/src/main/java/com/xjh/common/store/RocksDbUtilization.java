package com.xjh.common.store;

import com.xjh.common.utils.Logger;
import org.rocksdb.Options;
import org.rocksdb.RocksDBException;
import org.rocksdb.TtlDB;

import java.io.File;

import static com.xjh.common.store.DirUtils.workDir;

public class RocksDbUtilization {
    public static TtlDB getDB(String dbname) throws RocksDBException {
        String workDir = workDir();
        File home = new File(workDir + "database/kv/rocksdb/" + dbname);
        Logger.info("根目录:" + home.getAbsolutePath());
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
