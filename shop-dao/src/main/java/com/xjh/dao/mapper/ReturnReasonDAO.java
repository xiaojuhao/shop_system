package com.xjh.dao.mapper;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.google.inject.name.Named;
import com.xjh.common.utils.DateBuilder;
import com.xjh.common.utils.OrElse;
import com.xjh.common.utils.Result;
import com.xjh.dao.dataobject.ReturnReasonDO;
import com.xjh.dao.foundation.EntityUtils;
import com.xjh.dao.query.ReturnReasonQuery;
import com.zaxxer.hikari.HikariDataSource;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class ReturnReasonDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public Result<Integer> insert(ReturnReasonDO reason) {
        try {
            int i = Db.use(ds).insert(EntityUtils.create(reason));
            return Result.success(i);
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail(ex.getMessage());
        }
    }

    public Result<List<ReturnReasonDO>> selectList(ReturnReasonDO cond) {
        try {
            List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
            return Result.success(EntityUtils.convertList(list, ReturnReasonDO.class));
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail(ex.getMessage());
        }
    }

    public Result<List<ReturnReasonDO>> selectList(ReturnReasonQuery cond) {
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
                where.append(" and addtime >= ? ");
                params.add(DateBuilder.base(cond.getStartDate()).mills());
            }
            if (cond.getEndDate() != null) {
                where.append(" and addtime <= ? ");
                params.add(DateBuilder.base(cond.getEndDate()).plusDays(1).mills());
            }

            String sql = "select * from return_reason where 1=1 " + where
                    + " order by addtime desc "
                    + " limit " + (pageNo - 1) * pageSize + "," + pageSize;

            List<Entity> list = Db.use(ds).query(sql, params.toArray(new Object[0]));
            return Result.success(EntityUtils.convertList(list, ReturnReasonDO.class));
        } catch (Exception ex) {
            ex.printStackTrace();
            return Result.fail(ex.getMessage());
        }
    }
}
