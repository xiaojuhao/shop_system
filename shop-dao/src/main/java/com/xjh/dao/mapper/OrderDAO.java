package com.xjh.dao.mapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.OrElse;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.Order;
import com.xjh.dao.foundation.EntityUtils;
import com.xjh.dao.query.PageQueryOrderReq;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;

import static com.xjh.common.utils.CommonUtils.firstOf;

@Singleton
public class OrderDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public Result<Integer> insert(Order order) throws SQLException {
        // return Db.use(ds).insert(EntityUtils.create(order));
        List<Object> keys = Db.use(ds).insertForGeneratedKeys(EntityUtils.create(order));
        Integer key = CommonUtils.parseInt(firstOf(keys), null);
        if(key == null){
            return Result.fail("插入Order表失败");
        }
        return Result.success(key);
    }

    public Result<Integer> updateByOrderId(Order order) {
        try {
            int i = Db.use(ds).update(
                    EntityUtils.create(order),
                    EntityUtils.idCond(order)
            );
            return Result.success(i);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail(ex.getMessage());
        }
    }

    public Result<Order> selectByOrderId(Integer orderId) {
        try {
            if (orderId == null) {
                return Result.fail("入参错误");
            }
            Order cond = new Order();
            cond.setOrderId(orderId);
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            if (list.size() > 0) {
                return Result.success(convert(list.get(0)));
            }
            return Result.fail("订单不存在");
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail(ex.getMessage());
        }
    }

    public List<Order> pageQuery(PageQueryOrderReq cond) {
        try {
            int pageNo = OrElse.orGet(cond.getPageNo(), 1);
            int pageSize = OrElse.orGet(cond.getPageSize(), 20);
            StringBuilder where = new StringBuilder();
            List<Object> params = new ArrayList<>();
            if (cond.getOrderId() != null) {
                where.append(" and orderId = ?");
                params.add(cond.getOrderId());
            }
            if (cond.getStartDate() != null) {
                where.append(" and createtime >= ? ");
                params.add(DateBuilder.base(cond.getStartDate()).mills());
            }
            if (cond.getEndDate() != null) {
                where.append(" and createtime <= ? ");
                params.add(DateBuilder.base(cond.getEndDate()).plusDays(1).mills());
            }
            if (cond.getDeskId() != null) {
                where.append(" and deskId = ? ");
                params.add(cond.getDeskId());
            }
            if (cond.getAccountId() != null) {
                where.append(" and accountId = ? ");
                params.add(cond.getAccountId());
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
