package com.xjh.service.domain;

import java.sql.SQLException;
import java.time.LocalDateTime;

import com.alibaba.fastjson.JSONObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.xjh.common.enumeration.EnumOrderServeStatus;
import com.xjh.common.enumeration.EnumOrderStatus;
import com.xjh.common.enumeration.EnumOrderType;
import com.xjh.common.store.SequenceDatabase;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.valueobject.OrderDiscount;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.mapper.OrderDAO;
import com.xjh.service.domain.model.CreateOrderParam;

import cn.hutool.core.codec.Base64;

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

    public Order createOrder(CreateOrderParam param) throws SQLException {
        Order order = new Order();
        order.setOrderId(param.getOrderId());
        order.setDeskId(param.getDeskId());
        order.setOrderStatus(EnumOrderStatus.UNPAID.status);
        order.setStatus(EnumOrderServeStatus.START.status);
        order.setOrderType(EnumOrderType.NORMAL.type);
        order.setOrderDiscountInfo(Base64.encode(JSONObject.toJSONString(new OrderDiscount())));
        order.setMemberId(0L);
        order.setOrderCustomerNums(param.getCustomerNum());
        order.setAccountId(0L);
        order.setOrderErase(0D);
        order.setOrderRefund(0D);
        order.setOrderReduction(0D);
        order.setOrderHadpaid(0D);

        if (order.getCreateTime() == null) {
            order.setCreateTime(DateBuilder.now().mills());
        }
        orderDAO.insert(order);
        return order;
    }

    public Integer createNewOrderId() {
        LocalDateTime start = DateBuilder.base("2021-01-01 00:00:01").dateTime();
        String timeStr = DateBuilder.today().format("yyyyMMddHH");
        int diffHours = (int) DateBuilder.diffHours(start, DateBuilder.base(timeStr).dateTime());
        if (diffHours <= 0) {
            throw new RuntimeException("电脑日期设置有误:" + timeStr);
        }
        int nextId = nextId(timeStr);
        if (nextId >= 2 << 15) {
            throw new RuntimeException("循环次数已达最大上限:" + timeStr);
        }
        // 前17位保存时间，后15位保存序列号
        return diffHours << 15 | nextId;
    }

    public synchronized int nextId(String group) {
        return SequenceDatabase.nextId("orderId:sequence:" + group);
    }
}
