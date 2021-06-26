package com.xjh.service.domain;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.TransactionConfig;
import com.xjh.common.store.DeskKvDatabase;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.LogUtils;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.mapper.OrderDAO;

@Singleton
public class OrderService {
    @Inject
    OrderDAO orderDAO;

    public Order getOrder(String orderId) {
        try {
            return orderDAO.selectByOrderId(orderId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Order newOrder(Order order) throws SQLException {
        if (CommonUtils.isBlank(order.getOrderId())) {
            order.setOrderId(createNewOrderId());
        }
        if (order.getCreateTime() == null) {
            order.setCreateTime(DateBuilder.now().mills());
        }
        orderDAO.insert(order);
        return order;
    }

    public String createNewOrderId() {
        String date = DateBuilder.today().format("yyMMdd");
        long nextId = nextId("orderSequence" + date);
        return date + padding(nextId, 4);
    }

    public static void main(String[] args) {
        String date = DateBuilder.today().format("yy");
        for (int i = 0; i <= 10000; i++) {
            System.out.println(padding(i, 4));
        }
    }

    private static String padding(long val, int len) {
        StringBuilder sb = new StringBuilder();
        sb.append(val);
        while (sb.length() < len) {
            sb.insert(0, "0");
        }
        return sb.toString();
    }

    public synchronized long nextId(String group) {
        String key = "sequence_" + group;
        DatabaseEntry theKey = new DatabaseEntry(key.getBytes(StandardCharsets.UTF_8));
        DatabaseEntry theData = new DatabaseEntry();
        TransactionConfig txConfig = new TransactionConfig();
        txConfig.setSerializableIsolation(true);
        Database db = DeskKvDatabase.getDB();
        Transaction txn = db.getEnvironment().beginTransaction(null, txConfig);
        OperationStatus status = db.get(txn, theKey, theData, LockMode.DEFAULT);
        long newId = 0;
        try {
            if (status == OperationStatus.SUCCESS) {
                String value = new String(theData.getData());
                newId = CommonUtils.parseLong(value, 1L);
            } else if (status == OperationStatus.NOTFOUND) {
                newId = 1;
            }
            DatabaseEntry newData = new DatabaseEntry(String.valueOf(newId + 1).getBytes(StandardCharsets.UTF_8));
            db.put(txn, theKey, newData);
        } catch (Exception ex) {
            LogUtils.error("获取订单ID失败:" + group + "," + ex.getMessage());
            throw new RuntimeException("获取订单ID序列失败");
        }
        txn.commit();
        return newId;
    }
}
