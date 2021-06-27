package com.xjh.dao.mapper;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;

@Singleton
public class OrderDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public int insert(Order order) throws SQLException {
        return Db.use(ds).insert(EntityUtils.create(order));
    }

    public int updateByOrderId(Order order) {
        return 0;
    }

    public Order selectByOrderId(String orderId) throws SQLException {
        Order cond = new Order();
        cond.setOrderId(orderId);
        List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
        if (list.size() > 0) {
            return convert(list.get(0));
        }
        return null;
    }

    private Order convert(Entity entity) {
        return EntityUtils.convert(entity, Order.class);
    }
}
