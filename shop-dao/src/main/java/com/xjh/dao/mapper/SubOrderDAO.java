package com.xjh.dao.mapper;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.google.inject.name.Named;
import com.xjh.common.utils.CommonUtils;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.SubOrder;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.xjh.common.utils.CommonUtils.parseInt;

@Singleton
public class SubOrderDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public int countSubOrders(Integer orderId) throws SQLException {
        if (orderId == null) {
            return 0;
        }
        String sql = "select count(*) from suborder_list where orderId = " + orderId;
        Number number = Db.use(ds).queryNumber(sql);
        if (number != null) {
            return number.intValue();
        } else {
            return 0;
        }
    }

    public SubOrder findBySubOrderId(Integer subOrderId) {
        try {
            if (subOrderId == null) {
                return null;
            }
            SubOrder cond = new SubOrder();
            cond.setSubOrderId(subOrderId);
            return selectList(cond).stream().findFirst().orElse(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<SubOrder> findByOrderId(Integer orderId) {
        try {
            if (orderId == null) {
                return new ArrayList<>();
            }
            SubOrder cond = new SubOrder();
            cond.setOrderId(orderId);
            return selectList(cond);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Long firsSubOrderTime(Integer orderId) throws SQLException {
        if (orderId == null) {
            return null;
        }
        String sql = "select min(createtime) createtime" + " from suborder_list where orderId = " + orderId;
        Entity rs = Db.use(ds).queryOne(sql);
        if (rs == null) {
            return null;
        }
        return rs.getLong("createtime");
    }

    public Result<Integer> insert(SubOrder subOrder) throws SQLException {
        // return Db.use(ds).insert(EntityUtils.create(subOrder));
        Long id = Db.use(ds).insertForGeneratedKey(EntityUtils.create(subOrder));
        if (id == null) {
            return Result.fail("插入SubOrder失败");
        }
        return Result.success(parseInt(id, null));
    }

    public Result<Integer> updateById(SubOrder subOrder) {
        try {
            int i = Db.use(ds).update(EntityUtils.create(subOrder), EntityUtils.idCond(subOrder));
            return Result.success(i);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail(ex.getMessage());
        }
    }

    public List<SubOrder> selectList(SubOrder subOrder) {
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(subOrder));
            List<SubOrder> rs = EntityUtils.convertList(list, SubOrder.class);
            rs.sort(Comparator.comparing(SubOrder::getSubOrderId));
            return rs;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<SubOrder> selectByOrderIds(List<Integer> orderIds) {
        try {
            if (CommonUtils.isEmpty(orderIds)) {
                return new ArrayList<>();
            }
            String sql = "select * from suborder_list where orderId in (" + orderIds.stream().map(Object::toString).collect(Collectors.joining(",", "", "")) + ")";
            List<Entity> list = Db.use(ds).query(sql);
            return EntityUtils.convertList(list, SubOrder.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

}
