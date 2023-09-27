package com.xjh.dao.mapper;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.OrderDishes;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class OrderDishesDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public int insert(OrderDishes orderDishes) throws SQLException {
        return Db.use(ds).insert(EntityUtils.create(orderDishes));
    }

    public List<OrderDishes> selectByOrderId(Integer orderId) {
        if (orderId == null) {
            return new ArrayList<>();
        }
        OrderDishes cond = new OrderDishes();
        cond.setOrderId(orderId);
        return select(cond);
    }

    public List<OrderDishes> selectByOrderIds(List<Integer> orderIds) {
        try {
            if (CommonUtils.isEmpty(orderIds)) {
                return new ArrayList<>();
            }
            String sql = "select * from order_dishes_list where orderId in (" +
                    orderIds.stream().map(Object::toString)
                            .collect(Collectors.joining(",", "", ""))
                    + ")";
            List<Entity> list = Db.use(ds).query(sql);
            return EntityUtils.convertList(list, OrderDishes.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public<T> Result<List<T>> query(String sql, Class<T> clz){
        try{
            List<Entity> list = Db.use(ds).query(sql);
            return Result.success(EntityUtils.convertList(list, clz));
        } catch (Exception ex){
            ex.printStackTrace();
            return Result.fail("");
        }
    }

    public List<OrderDishes> select(OrderDishes example) {
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(example));
            return CommonUtils.collect(list, it -> EntityUtils.convert(it, OrderDishes.class));
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public OrderDishes selectById(Integer id) {
        if (id == null) {
            return null;
        }
        OrderDishes cond = new OrderDishes();
        cond.setOrderDishesId(id.longValue());
        return CommonUtils.firstOf(select(cond));
    }

    public List<OrderDishes> selectByIds(List<Integer> ids) {
        try {
            List<OrderDishes> list = new ArrayList<>();
            for (Integer id : ids) {
                OrderDishes d = selectById(id);
                if (d != null) {
                    list.add(d);
                }
            }
            return list;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public int updateByPK(OrderDishes orderDishes) throws SQLException {
        return Db.use(ds).update(
                EntityUtils.create(orderDishes),
                EntityUtils.idCond(orderDishes)
        );
    }
}
