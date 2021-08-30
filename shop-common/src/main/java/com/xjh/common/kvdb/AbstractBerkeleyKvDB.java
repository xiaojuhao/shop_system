package com.xjh.common.kvdb;

import com.alibaba.fastjson.JSON;
import com.sleepycat.je.*;
import com.xjh.common.store.BerkeleyDBUtils;
import com.xjh.common.store.SequenceDatabase;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Holder;

import java.nio.charset.StandardCharsets;

public abstract class AbstractBerkeleyKvDB implements KvDB {
    ThreadLocal<Transaction> transaction = new ThreadLocal<>();

    @Override
    public void beginTransaction() {
        TransactionConfig txConfig = new TransactionConfig();
        txConfig.setSerializableIsolation(true);
        Database db = getDB();
        Transaction txn = db.getEnvironment().beginTransaction(null, txConfig);
        transaction.set(txn);
    }

    @Override
    public void commit() {
        Transaction txn = transaction.get();
        if (txn != null) {
            txn.commit();
        }
        transaction.remove();
    }

    @Override
    public void put(String key, Object val) {
        DatabaseEntry theKey = new DatabaseEntry(key.getBytes(StandardCharsets.UTF_8));
        DatabaseEntry newData = new DatabaseEntry(JSON.toJSONString(val).getBytes(StandardCharsets.UTF_8));
        getDB().put(transaction.get(), theKey, newData);
    }

    @Override
    public void remove(String key) {
        DatabaseEntry theKey = new DatabaseEntry(key.getBytes(StandardCharsets.UTF_8));
        getDB().delete(transaction.get(), theKey);
    }

    @Override
    public<T> T get(String key, Class<T> clz) {
        DatabaseEntry theKey = new DatabaseEntry(key.getBytes(StandardCharsets.UTF_8));
        DatabaseEntry theData = new DatabaseEntry();
        getDB().get(null, theKey, theData, LockMode.READ_UNCOMMITTED_ALL);
        try {
            if (theData.getData() != null) {
                String value = new String(theData.getData());
                if (CommonUtils.isNotBlank(value)) {
                    return JSON.parseObject(value, clz);
                }
            } else {
                return null;
            }
        } catch (Exception ex) {
            throw new RuntimeException("查询数据失败:" + key);
        }
        return null;
    }

    Holder<Database> staticInst = new Holder<>();

    private Database getDB() {
        if (staticInst.get() != null) {
            return staticInst.get();
        }
        synchronized (SequenceDatabase.class) {
            if (staticInst.get() != null) {
                return staticInst.get();
            }
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setTransactional(true);
            dbConfig.setAllowCreate(true);
            Database db = BerkeleyDBUtils.getEnv()
                    .openDatabase(null, getDbName(), dbConfig);
            return staticInst.hold(db);
        }
    }

    public abstract String getDbName();
}