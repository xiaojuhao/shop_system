package com.xjh.service.store;

import com.xjh.common.kvdb.Committable;
import com.xjh.common.kvdb.cart.CartKvDB;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.Cart;

public class CartStore {
    static CartKvDB cartKvDB = CartKvDB.inst();

    public static Cart getCart(Integer deskId) {
        String key = "cart_" + deskId;
        return cartKvDB.get(key, Cart.class);
//        DatabaseEntry theKey = new DatabaseEntry(key.getBytes(StandardCharsets.UTF_8));
//        DatabaseEntry theData = new DatabaseEntry();
//        TransactionConfig txConfig = new TransactionConfig();
//        txConfig.setSerializableIsolation(true);
//        Database db = getDB();
//        Transaction txn = db.getEnvironment().beginTransaction(null, txConfig);
//        OperationStatus status = db.get(txn, theKey, theData, LockMode.DEFAULT);
//        try {
//            if (theData.getData() != null) {
//                String value = new String(theData.getData());
//                if (CommonUtils.isNotBlank(value)) {
//                    return JSON.parseObject(value, Cart.class);
//                }
//            }
//        } catch (Exception ex) {
//            Logger.error("获取购物车失败:" + deskId + "," + ex.getMessage());
//            throw new RuntimeException("获取购物车失败");
//        } finally {
//            txn.commit();
//        }
//        return null;
    }

    public static Result<String> saveCart(Cart cart) {
        if (cart == null) {
            return Result.fail("保存失败,入参错误");
        }
        String key = "cart_" + cart.getDeskId();
        Committable committable = cartKvDB.beginTransaction();
        cartKvDB.put(key, cart);
        cartKvDB.commit(committable);
        return Result.success(null);
//        DatabaseEntry theKey = new DatabaseEntry(key.getBytes(StandardCharsets.UTF_8));
//        TransactionConfig txConfig = new TransactionConfig();
//        txConfig.setSerializableIsolation(true);
//        Database db = getDB();
//        Transaction txn = db.getEnvironment().beginTransaction(null, txConfig);
//        try {
//            DatabaseEntry newData = new DatabaseEntry(JSON.toJSONString(cart).getBytes(StandardCharsets.UTF_8));
//            db.put(txn, theKey, newData);
//            return Result.success(null);
//        } catch (Exception ex) {
//            Logger.error("保存购物车失败:" + ex.getMessage());
//            return Result.fail("保存购物车失败:" + ex.getMessage());
//        } finally {
//            txn.commit();
//        }
    }

    public static Result<String> clearCart(Integer deskId) {
        if (deskId == null) {
            return Result.fail("保存失败,入参错误");
        }
        String key = "cart_" + deskId;
        Committable committable = cartKvDB.beginTransaction();
        cartKvDB.remove(key);
        cartKvDB.commit(committable);
        return Result.fail("");
//        DatabaseEntry theKey = new DatabaseEntry(key.getBytes(StandardCharsets.UTF_8));
//        TransactionConfig txConfig = new TransactionConfig();
//        txConfig.setSerializableIsolation(true);
//        Database db = getDB();
//        Transaction txn = db.getEnvironment().beginTransaction(null, txConfig);
//        try {
//            db.delete(txn, theKey);
//            return Result.success("清空购物车成功");
//        } catch (Exception ex) {
//            Logger.error("清空购物车失败:" + ex.getMessage());
//            return Result.fail("清空购物车失败:" + ex.getMessage());
//        } finally {
//            txn.commit();
//        }
    }

//    static final String dbName = "cart";
//    static Database staticInst = null;
//
//    private static Database getDB() {
//        if (staticInst != null) {
//            return staticInst;
//        }
//        synchronized (SequenceDatabase.class) {
//            if (staticInst != null) {
//                return staticInst;
//            }
//            DatabaseConfig dbConfig = new DatabaseConfig();
//            dbConfig.setTransactional(true);
//            dbConfig.setAllowCreate(true);
//            Database db = BerkeleyDBUtils.getEnv()
//                    .openDatabase(null, dbName, dbConfig);
//            staticInst = db;
//            return db;
//        }
//    }
}
