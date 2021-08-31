package com.xjh.common.kvdb;

import java.nio.charset.StandardCharsets;

import com.alibaba.fastjson.JSON;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.TransactionConfig;
import com.xjh.common.store.BerkeleyDBUtils;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Holder;

public abstract class AbstractBerkeleyKvDB<T> implements KvDB<T> {
    ThreadLocal<Transaction> transaction = new ThreadLocal<>();

    @Override
    public Committable beginTransaction() {
        if (transaction.get() != null) {
            return () -> {
            };
        }
        TransactionConfig txConfig = new TransactionConfig();
        txConfig.setSerializableIsolation(true);
        Database db = getDB();
        Transaction txn = db.getEnvironment().beginTransaction(null, txConfig);
        transaction.set(txn);
        return () -> {
            txn.commit();
            transaction.remove();
        };
    }

    @Override
    public void commit(Committable committable) {
        if (committable != null) {
            committable.commit();
        }
    }

    @Override
    public void put(String key, T val) {
        DatabaseEntry theKey = new DatabaseEntry(key.getBytes(StandardCharsets.UTF_8));
        DatabaseEntry newData = new DatabaseEntry(JSON.toJSONString(val).getBytes(StandardCharsets.UTF_8));
        OperationStatus status = getDB().put(transaction.get(), theKey, newData);
        // Logger.info("保存KV数据, key=" + key +", status="+ status +", val=" + CommonUtils.reflectString(val));
    }

    @Override
    public void remove(String key) {
        DatabaseEntry theKey = new DatabaseEntry(key.getBytes(StandardCharsets.UTF_8));
        OperationStatus status = getDB().delete(transaction.get(), theKey);
        // Logger.info("刪除KV数据, key=" + key +", status=" + status);
    }

    @Override
    public T get(String key, Class<T> clz) {
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

    Holder<Database> instHolder = new Holder<>();

    private Database getDB() {
        if (instHolder.get() != null) {
            return instHolder.get();
        }
        synchronized (AbstractBerkeleyKvDB.class) {
            if (instHolder.get() != null) {
                return instHolder.get();
            }
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setTransactional(true);
            dbConfig.setAllowCreate(true);
            Database db = BerkeleyDBUtils.getEnv()
                    .openDatabase(null, getDbName(), dbConfig);
            return instHolder.hold(db);
        }
    }

    public abstract String getDbName();
}