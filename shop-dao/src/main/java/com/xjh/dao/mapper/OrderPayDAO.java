package com.xjh.dao.mapper;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.alibaba.fastjson.JSON;
import com.google.inject.name.Named;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Logger;
import com.xjh.dao.dataobject.OrderPay;
import com.xjh.dao.foundation.EntityUtils;
import com.xjh.dao.query.OrderPayQuery;
import com.zaxxer.hikari.HikariDataSource;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<OrderPay> selectByOrderIds(List<Integer> orderIds) {
        try {
            if (CommonUtils.isEmpty(orderIds)) {
                return new ArrayList<>();
            }
            String sql = "select * from order_pays where orderId in (" +
                    orderIds.stream().map(Object::toString)
                            .collect(Collectors.joining(",", "", ""))
                    + ")";
            List<Entity> list = Db.use(ds).query(sql);
            return EntityUtils.convertList(list, OrderPay.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
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

    public int deleteBy(OrderPayQuery cond) throws SQLException {
        StringBuilder sql = new StringBuilder("delete from order_pays where orderId = ? ");
        List<Object> params = new ArrayList<>();
        params.add(cond.getOrderId());
        if (cond.getPaymentStatus() != null) {
            sql.append(" and paymentStatus = ? ");
            params.add(cond.getPaymentStatus());
        }
        if (CommonUtils.isNotEmpty(cond.getExcludePayMethods())) {
            sql.append(" and paymentMethod in (")
                    .append(cond.getExcludePayMethods().stream().map(i -> i + "").collect(Collectors.joining(",")))
                    .append(")");
        }
        Logger.info("删除支付记录: " + JSON.toJSON(cond));
        return Db.use(ds).execute(sql.toString(), params.toArray(new Object[0]));
    }
}
