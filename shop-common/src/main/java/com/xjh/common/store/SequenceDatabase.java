package com.xjh.common.store;

import java.nio.charset.StandardCharsets;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.TransactionConfig;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.LogUtils;

public class SequenceDatabase {
    public static synchronized int nextId(String group) {
        String key = "sequence_" + group;
        DatabaseEntry theKey = new DatabaseEntry(key.getBytes(StandardCharsets.UTF_8));
        DatabaseEntry theData = new DatabaseEntry();
        TransactionConfig txConfig = new TransactionConfig();
        txConfig.setSerializableIsolation(true);
        Database db = DeskKvDatabase.getDB();
        Transaction txn = db.getEnvironment().beginTransaction(null, txConfig);
        OperationStatus status = db.get(txn, theKey, theData, LockMode.DEFAULT);
        int newId = 0;
        try {
            if (status == OperationStatus.SUCCESS) {
                String value = new String(theData.getData());
                newId = CommonUtils.parseInt(value, 1);
            } else if (status == OperationStatus.NOTFOUND) {
                newId = 1;
            }
            DatabaseEntry newData = new DatabaseEntry(String.valueOf(newId + 1).getBytes(StandardCharsets.UTF_8));
            db.put(txn, theKey, newData);
        } catch (Exception ex) {
            LogUtils.error("获取订单ID失败:" + group + "," + ex.getMessage());
            throw new RuntimeException("获取订单ID序列失败");
        } finally {
            txn.commit();
        }
        return newId;
    }
}
