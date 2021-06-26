package com.xjh.dao.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.xjh.common.utils.CommonUtils;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.dao.foundation.EntityUtils;
import com.xjh.dao.mapper.OrderDishesDAO;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;

@Singleton
public class OrderDishesDAOImpl implements OrderDishesDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;


    @Override
    public List<OrderDishes> selectByOrderId(String orderId) {
        if (CommonUtils.isBlank(orderId)) {
            return new ArrayList<>();
        }
        OrderDishes cond = new OrderDishes();
        cond.setOrderId(orderId);
        return select(cond);
    }

    @Override
    public List<OrderDishes> select(OrderDishes example) {
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(example));
            return CommonUtils.collect(list, it -> EntityUtils.convert(it, OrderDishes.class));
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }
}
