package com.xjh.service.domain;

import java.sql.SQLException;
import java.time.LocalDateTime;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.xjh.common.store.SequenceDatabase;
import com.xjh.common.utils.DateBuilder;
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
        int diffHours = (int) DateBuilder.diffHours(start, today);
        if (diffHours <= 0) {
            throw new RuntimeException("电脑日期设置有误:" + today);
        }
        int nextId = nextId(todayStr);
        // 前17位保存时间，后15位保存序列号
        return diffHours << 15 | (nextId % 32767);
    }

    public synchronized int nextId(String group) {
        return SequenceDatabase.nextId("orderId:sequence:" + group);
    }
}
