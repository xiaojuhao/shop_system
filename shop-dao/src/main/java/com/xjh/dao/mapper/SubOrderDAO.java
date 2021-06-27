package com.xjh.dao.mapper;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.inject.name.Named;
import com.xjh.dao.dataobject.SubOrder;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;

@Singleton
public class SubOrderDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public int insert(SubOrder subOrder) throws SQLException {
        return Db.use(ds).insert(EntityUtils.create(subOrder));
    }

    public int updateById(SubOrder subOrder) throws SQLException {
        return Db.use(ds).update(
                EntityUtils.create(subOrder),
                EntityUtils.idCond(subOrder));
    }

    public List<SubOrder> selectList(SubOrder subOrder) throws SQLException {
        List<Entity> list = Db.use(ds).find(EntityUtils.create(subOrder));
        return EntityUtils.convertList(list, SubOrder.class);
    }

}
