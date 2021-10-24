package com.xjh.dao.mapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.xjh.common.utils.CommonUtils;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.foundation.EntityUtils;
import com.xjh.dao.query.PageQueryOrderReq;
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

    public int updateByOrderId(Order order) throws SQLException {
        return Db.use(ds).update(
                EntityUtils.create(order),
                EntityUtils.idCond(order)
        );
    }

    public Order selectByOrderId(Integer orderId) throws SQLException {
        if (orderId == null) {
            return null;
        }
        Order cond = new Order();
        cond.setOrderId(orderId);
        List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
        if (list.size() > 0) {
            return convert(list.get(0));
        }
        return null;
    }

    public List<Order> pageQuery(PageQueryOrderReq cond) {
        try {
            int pageNo = CommonUtils.orElse(cond.getPageNo(), 1);
            int pageSize = CommonUtils.orElse(cond.getPageSize(), 20);
            StringBuilder where = new StringBuilder();
            List<Object> params = new ArrayList<>();
            if (cond.getOrderId() != null) {
                where.append(" and orderId = ?");
                params.add(cond.getOrderId());
            }
            if (cond.getStartTime() != null) {
                where.append(" and createtime >= ? ");
                params.add(cond.getStartTime());
            }
            if (cond.getEndTime() != null) {
                where.append(" and createtime <= ? ");
                params.add(cond.getEndTime());
            }
            String sql = "select * from order_list where 1=1 " + where
                    + " order by createtime desc "
                    + " limit " + (pageNo - 1) * pageSize + "," + pageSize;

            // System.out.println(sql + ", " + JSON.toJSONString(params));
            List<Entity> list = Db.use(ds).query(sql, params.toArray(new Object[0]));
            return EntityUtils.convertList(list, Order.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    private Order convert(Entity entity) {
        return EntityUtils.convert(entity, Order.class);
    }
}
