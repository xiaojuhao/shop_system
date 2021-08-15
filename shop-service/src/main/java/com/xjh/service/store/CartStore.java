package com.xjh.service.store;

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
import com.xjh.common.store.SequenceDatabase;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Logger;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.Cart;

public class CartStore {
    public static Cart getCart(Integer deskId) {
        String key = "cart_" + deskId;
        DatabaseEntry theKey = new DatabaseEntry(key.getBytes(StandardCharsets.UTF_8));
        DatabaseEntry theData = new DatabaseEntry();
        TransactionConfig txConfig = new TransactionConfig();
        txConfig.setSerializableIsolation(true);
        Database db = getDB();
        Transaction txn = db.getEnvironment().beginTransaction(null, txConfig);
        OperationStatus status = db.get(txn, theKey, theData, LockMode.DEFAULT);
        try {
            if (theData.getData() != null) {
                String value = new String(theData.getData());
                if (CommonUtils.isNotBlank(value)) {
                    return JSON.parseObject(value, Cart.class);
                }
            }
        } catch (Exception ex) {
            Logger.error("获取购物车失败:" + deskId + "," + ex.getMessage());
            throw new RuntimeException("获取购物车失败");
        } finally {
            txn.commit();
        }
        return null;
    }

    public static Result<String> saveCart(Cart cart) {
        if (cart == null) {
            return Result.fail("保存失败,入参错误");
        }
        String key = "cart_" + cart.getDeskId();
        DatabaseEntry theKey = new DatabaseEntry(key.getBytes(StandardCharsets.UTF_8));
        TransactionConfig txConfig = new TransactionConfig();
        txConfig.setSerializableIsolation(true);
        Database db = getDB();
        Transaction txn = db.getEnvironment().beginTransaction(null, txConfig);
        try {
            DatabaseEntry newData = new DatabaseEntry(JSON.toJSONString(cart).getBytes(StandardCharsets.UTF_8));
            db.put(txn, theKey, newData);
            return Result.success(null);
        } catch (Exception ex) {
            Logger.error("保存购物车失败:" + ex.getMessage());
            return Result.fail("保存购物车失败:" + ex.getMessage());
        } finally {
            txn.commit();
        }
    }

    public static Result<String> clearCart(Integer deskId) {
        if (deskId == null) {
            return Result.fail("保存失败,入参错误");
        }
        String key = "cart_" + deskId;
        DatabaseEntry theKey = new DatabaseEntry(key.getBytes(StandardCharsets.UTF_8));
        TransactionConfig txConfig = new TransactionConfig();
        txConfig.setSerializableIsolation(true);
        Database db = getDB();
        Transaction txn = db.getEnvironment().beginTransaction(null, txConfig);
        try {
            db.delete(txn, theKey);
            return Result.success(null);
        } catch (Exception ex) {
            Logger.error("清空购物车失败:" + ex.getMessage());
            return Result.fail("清空购物车失败:" + ex.getMessage());
        } finally {
            txn.commit();
        }
    }

    static final String dbName = "cart";
    static Database staticInst = null;

    private static Database getDB() {
        if (staticInst != null) {
            return staticInst;
        }
        synchronized (SequenceDatabase.class) {
            if (staticInst != null) {
                return staticInst;
            }
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setTransactional(true);
            dbConfig.setAllowCreate(true);
            Database db = BerkeleyDBUtils.getEnv()
                    .openDatabase(null, dbName, dbConfig);
            staticInst = db;
            return db;
        }
    }
}
