package com.xjh.common.kvdb;

import com.alibaba.fastjson.JSON;
import com.sleepycat.je.*;
import com.xjh.common.store.BerkeleyDBUtils;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Holder;

import java.nio.charset.StandardCharsets;

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
        String storeV;
        if(val instanceof String){
            storeV = val.toString();
        }else {
            storeV = JSON.toJSONString(val);
        }
        DatabaseEntry newData = new DatabaseEntry(storeV.getBytes(StandardCharsets.UTF_8));
        OperationStatus status = getDB().put(transaction.get(), theKey, newData);
        // Logger.info("保存KV数据, key=" + key +", status="+ status +", val=" + CommonUtils.reflectString(val));

        // 使用StoredClassCatalog存储数据
//        DatabaseConfig catalogDBConfig = new DatabaseConfig();
//        catalogDBConfig.setTransactional(true);
//        catalogDBConfig.setAllowCreate(true);
//        Database catalogDB = BerkeleyDBUtils.getEnv().openDatabase(null, "catalog_db", catalogDBConfig);
//        StoredClassCatalog catalog = new StoredClassCatalog(catalogDB);
//        EntryBinding binding = new SerialBinding(catalog, Object.class);
//
//        DatabaseEntry dbentry = new DatabaseEntry();
//        binding.objectToEntry(val, dbentry); // 将对象存入dbentry
//        getDB().put(null, theKey, dbentry); // 存入数据库
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
                if(clz == String.class){
                    return (T)value;
                }
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
            long startTime = System.currentTimeMillis();
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setTransactional(true);
            dbConfig.setAllowCreate(true);
            Database db = BerkeleyDBUtils.getEnv()
                    .openDatabase(null, getDbName(), dbConfig);
            System.out.println("初始化BDB（" + getDbName() + "）, 耗时 " + (System.currentTimeMillis() - startTime) + "毫秒");
            return instHolder.hold(db);
        }
    }

    public abstract String getDbName();
}