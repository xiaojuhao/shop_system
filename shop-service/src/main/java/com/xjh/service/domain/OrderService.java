package com.xjh.service.domain;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.mapper.OrderDAO;

@Singleton
public class OrderService {
    @Inject
    OrderDAO orderDAO;

    public Order getOrder(String orderId) {
        return orderDAO.selectByOrderId(orderId);
    }
}
