package com.xjh.dao.mapper;

import com.xjh.dao.dataobject.Order;

public interface OrderDAO {
    int insert(Order order);

    int updateByOrderId(Order order);

    Order selectByOrderId(String orderId);

}
