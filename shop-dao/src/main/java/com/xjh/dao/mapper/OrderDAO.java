package com.xjh.dao.mapper;

import java.sql.SQLException;

import com.xjh.dao.dataobject.Order;

public interface OrderDAO {
    int insert(Order order) throws SQLException;

    int updateByOrderId(Order order);

    Order selectByOrderId(String orderId) throws SQLException;

}
