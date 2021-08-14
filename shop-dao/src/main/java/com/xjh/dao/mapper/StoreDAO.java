package com.xjh.dao.mapper;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.xjh.dao.dataobject.Store;
import com.xjh.dao.foundation.EntityUtils;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;

@Singleton
public class StoreDAO {
    @Inject
    @Named("mysql")
    HikariDataSource ds;

    public List<Store> selectList(Store cond) throws SQLException {
        List<Entity> list = Db.use(ds).find(EntityUtils.create(cond));
        return EntityUtils.convertList(list, Store.class);
    }

}
