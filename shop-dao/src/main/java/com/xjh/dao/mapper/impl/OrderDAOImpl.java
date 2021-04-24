package com.xjh.dao.mapper.impl;

import javax.inject.Inject;

import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.foundation.EntityUtils;
import com.xjh.dao.mapper.OrderDAO;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.db.Entity;

@Singleton
public class OrderDAOImpl implements OrderDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    @Override
    public int insert(Order order) {
        return 0;
    }

    @Override
    public int updateByOrderId(Order order) {
        return 0;
    }

    @Override
    public Order selectByOrderId(String orderId) {
        return null;
    }

    private Order convertReader(Entity entity) {
        Order order = new Order();
        EntityUtils.convert(entity, order);
        return order;
    }

}
