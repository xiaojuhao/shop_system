package com.xjh.service.domain;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;

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

    public Order getOrder(Integer orderId) {
        try {
            return orderDAO.selectByOrderId(orderId);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Order newOrder(Order order) throws SQLException {
        if (order.getOrderId() == null) {
            order.setOrderId(createNewOrderId());
        }
        if (order.getCreateTime() == null) {
            order.setCreateTime(DateBuilder.now().mills());
        }
        orderDAO.insert(order);
        return order;
    }

    public Integer createNewOrderId() {
        LocalDateTime start = DateBuilder.base("2021-01-01 00:00:01").dateTime();
        LocalDateTime today = DateBuilder.now().dateTime();
        String todayStr = DateBuilder.today().format("yyyyMMdd");
        int diffDays = (int) DateBuilder.diffDays(start, today);
        if (diffDays <= 0) {
            throw new RuntimeException("电脑日期设置有误:" + today);
        }
        int nextId = nextId("orderId:sequence:" + todayStr);
        // 前16位保存时间，后16位保存序列号
        int val = diffDays << 16 | (nextId % 65535);
        return val;
    }

    public static void main(String[] args) {
        OrderService service = new OrderService();
        for (int i = 0; i < 9999; i++) {
            System.out.println(service.createNewOrderId());
        }
    }

    public synchronized int nextId(String group) {
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
            db.close();
        }
        return newId;
    }
}
