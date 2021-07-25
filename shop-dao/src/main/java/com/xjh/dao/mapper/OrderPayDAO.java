package com.xjh.dao.mapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.inject.name.Named;
import com.xjh.common.utils.CommonUtils;
import com.xjh.dao.dataobject.OrderPay;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;

@Singleton
public class OrderPayDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public int insert(OrderPay orderPay) throws SQLException {
        return Db.use(ds).insert(EntityUtils.create(orderPay));
    }

    public List<OrderPay> selectByOrderId(Integer orderId) {
        if (orderId == null) {
            return new ArrayList<>();
        }
        OrderPay cond = new OrderPay();
        cond.setOrderId(orderId);
        return selectList(cond);
    }

    public List<OrderPay> selectList(OrderPay example) {
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(example));
            return CommonUtils.collect(list, it -> EntityUtils.convert(it, OrderPay.class));
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }
}
