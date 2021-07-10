package com.xjh.service.domain;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.xjh.dao.dataobject.OrderPay;
import com.xjh.dao.mapper.OrderPayDAO;

@Singleton
public class OrderPayService {
    @Inject
    OrderPayDAO orderPayDAO;

    public int insert(OrderPay orderPay) throws SQLException {
        return orderPayDAO.insert(orderPay);
    }

    public List<OrderPay> selectByOrderId(Integer orderId) {
        if (orderId == null) {
            return new ArrayList<>();
        }
        return orderPayDAO.selectByOrderId(orderId);
    }

    public List<OrderPay> selectList(OrderPay example) {
        return orderPayDAO.selectList(example);
    }
}
